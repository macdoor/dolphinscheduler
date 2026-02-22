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

import org.apache.dolphinscheduler.plugin.task.api.model.ResourceInfo;
import org.apache.dolphinscheduler.plugin.task.api.parameters.AbstractParameters;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class FlinkSqlGatewayParameters extends AbstractParameters {

    /**
     * example: jdbc:flink://host:port
     */
    private String flinkJdbcUrl;

    /**
     * optional: username/password or other jdbc properties
     */
    private Map<String, String> jdbcProperties;

    /**
     * optional init sql script
     */
    private String initScript;

    /**
     * main sql script, required
     */
    private String rawScript;

    /**
     * default: ;
     */
    private String statementSeparator = ";";

    /**
     * print query result rows count in log, default 0 means do not print rows
     */
    private int maxPrintRows = 0;

    public Properties toJdbcProperties() {
        Properties props = new Properties();
        if (jdbcProperties != null) {
            jdbcProperties.forEach((k, v) -> {
                if (k != null && v != null) {
                    props.put(k, v);
                }
            });
        }
        return props;
    }

    @Override
    public boolean checkParameters() {
        return StringUtils.isNotBlank(flinkJdbcUrl) && StringUtils.isNotBlank(rawScript);
    }

    @Override
    public List<ResourceInfo> getResourceFilesList() {
        return Collections.emptyList();
    }

    public String getFlinkJdbcUrl() {
        return flinkJdbcUrl;
    }

    public void setFlinkJdbcUrl(String flinkJdbcUrl) {
        this.flinkJdbcUrl = flinkJdbcUrl;
    }

    public Map<String, String> getJdbcProperties() {
        return jdbcProperties;
    }

    public void setJdbcProperties(Map<String, String> jdbcProperties) {
        this.jdbcProperties = jdbcProperties;
    }

    public String getInitScript() {
        return initScript;
    }

    public void setInitScript(String initScript) {
        this.initScript = initScript;
    }

    public String getRawScript() {
        return rawScript;
    }

    public void setRawScript(String rawScript) {
        this.rawScript = rawScript;
    }

    public String getStatementSeparator() {
        return statementSeparator;
    }

    public void setStatementSeparator(String statementSeparator) {
        this.statementSeparator = statementSeparator;
    }

    public int getMaxPrintRows() {
        return maxPrintRows;
    }

    public void setMaxPrintRows(int maxPrintRows) {
        this.maxPrintRows = maxPrintRows;
    }
}

