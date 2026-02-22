/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dolphinscheduler.plugin.task.flinksqlgateway;

import org.apache.dolphinscheduler.common.utils.JSONUtils;
import org.apache.dolphinscheduler.plugin.task.api.AbstractTask;
import org.apache.dolphinscheduler.plugin.task.api.TaskCallBack;
import org.apache.dolphinscheduler.plugin.task.api.TaskConstants;
import org.apache.dolphinscheduler.plugin.task.api.TaskException;
import org.apache.dolphinscheduler.plugin.task.api.TaskExecutionContext;
import org.apache.dolphinscheduler.plugin.task.api.model.Property;
import org.apache.dolphinscheduler.plugin.task.api.parameters.AbstractParameters;
import org.apache.dolphinscheduler.plugin.task.api.utils.ParameterUtils;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
public class FlinkSqlGatewayTask extends AbstractTask {

    private final TaskExecutionContext taskExecutionContext;
    private FlinkSqlGatewayParameters parameters;

    private volatile Connection connection;
    private volatile Statement statement;

    public FlinkSqlGatewayTask(TaskExecutionContext taskExecutionContext) {
        super(taskExecutionContext);
        this.taskExecutionContext = taskExecutionContext;
    }

    @Override
    public void init() {
        parameters =
                JSONUtils.parseObject(taskExecutionContext.getTaskParams(), FlinkSqlGatewayParameters.class);

        // Replace parameter placeholders (e.g. ${system.biz.date}, $[yyyyMMdd]); empty params still allow time placeholders
        if (parameters != null) {
            Map<String, Property> paramsMap = taskExecutionContext.getPrepareParamsMap();
            Map<String, String> stringParams = ParameterUtils.convert(paramsMap);

            if (StringUtils.isNotBlank(parameters.getFlinkJdbcUrl())) {
                parameters.setFlinkJdbcUrl(ParameterUtils.convertParameterPlaceholders(parameters.getFlinkJdbcUrl(), stringParams));
            }
            if (StringUtils.isNotBlank(parameters.getInitScript())) {
                parameters.setInitScript(ParameterUtils.convertParameterPlaceholders(parameters.getInitScript(), stringParams));
            }
            if (StringUtils.isNotBlank(parameters.getRawScript())) {
                parameters.setRawScript(ParameterUtils.convertParameterPlaceholders(parameters.getRawScript(), stringParams));
            }
        }

        log.info("Initialize flink sqlgateway task params {}", JSONUtils.toPrettyJsonString(parameters));

        if (parameters == null || !parameters.checkParameters()) {
            throw new TaskException("flink sqlgateway task params is not valid");
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return parameters;
    }

    @Override
    public void handle(TaskCallBack taskCallBack) throws TaskException {
        try {
            Properties props = parameters.toJdbcProperties();
            connection = DriverManager.getConnection(parameters.getFlinkJdbcUrl(), props);
            statement = connection.createStatement();

            executeScriptIfPresent(parameters.getInitScript(), "init");
            executeScriptIfPresent(parameters.getRawScript(), "main");

            setExitStatusCode(TaskConstants.EXIT_CODE_SUCCESS);
        } catch (Exception e) {
            if (exitStatusCode == TaskConstants.EXIT_CODE_KILL) {
                log.info("flink sqlgateway task has been killed");
                return;
            }
            setExitStatusCode(TaskConstants.EXIT_CODE_FAILURE);
            throw new TaskException("Execute flink sqlgateway task failed", e);
        } finally {
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    @Override
    public void cancel() throws TaskException {
        exitStatusCode = TaskConstants.EXIT_CODE_KILL;
        try {
            if (statement != null) {
                statement.cancel();
            }
        } catch (Exception e) {
            throw new TaskException("Cancel flink sqlgateway task failed", e);
        } finally {
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    private void executeScriptIfPresent(String script, String tag) throws Exception {
        if (StringUtils.isBlank(script)) {
            return;
        }
        List<String> sqlList = splitSql(script, parameters.getStatementSeparator());
        for (int i = 0; i < sqlList.size(); i++) {
            String sql = sqlList.get(i);
            String trimmed = sql == null ? "" : sql.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            log.info("[{}] execute #{} sql: {}", tag, i + 1, trimmed);

            boolean hasResultSet = statement.execute(trimmed);
            if (hasResultSet) {
                try (ResultSet rs = statement.getResultSet()) {
                    int row = 0;
                    int maxPrint = Math.max(parameters.getMaxPrintRows(), 0);
                    while (rs != null && rs.next() && row < maxPrint) {
                        row++;
                        // avoid log explosion, print row index only
                        log.info("[{}] query result row {}", tag, row);
                    }
                    if (maxPrint > 0) {
                        log.info("[{}] query printed rows: {}", tag, row);
                    }
                }
            } else {
                int updateCount = statement.getUpdateCount();
                log.info("[{}] updateCount={}", tag, updateCount);
            }
        }
    }

    private static List<String> splitSql(String script, String separator) {
        String sep = StringUtils.isBlank(separator) ? ";" : separator;
        return Arrays.stream(script.split(java.util.regex.Pattern.quote(sep)))
                .collect(Collectors.toList());
    }

    private static void closeQuietly(AutoCloseable c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (Exception ignored) {
            // ignored
        }
    }
}

