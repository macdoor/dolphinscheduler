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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class FlinkSqlGatewayParameters extends AbstractParameters {

    /** Script source: inline SQL string (used for both main and init script) */
    public static final String SCRIPT_SOURCE_SCRIPT = "SCRIPT";
    /** Script source: resource center file (used for both main and init script) */
    public static final String SCRIPT_SOURCE_FILE = "FILE";

    /**
     * example: jdbc:flink://host:port
     */
    private String flinkJdbcUrl;

    /**
     * optional: username/password or other jdbc properties
     */
    private Map<String, String> jdbcProperties;

    /**
     * init script type: SCRIPT = inline sql string, FILE = resource center file
     */
    private String initScriptType = SCRIPT_SOURCE_SCRIPT;

    /**
     * optional init sql script (when initScriptType=SCRIPT), or read-only display (when FILE)
     */
    private String initScript;

    /**
     * resource list for init script file (when initScriptType=FILE), size 1
     */
    private List<ResourceInfo> initScriptResourceList;

    /**
     * main script type: SCRIPT = inline sql string, FILE = resource center file
     */
    private String rawScriptType = SCRIPT_SOURCE_SCRIPT;

    /**
     * main sql script (when rawScriptType=SCRIPT), or read-only display content (when rawScriptType=FILE)
     */
    private String rawScript;

    /**
     * resource list for main script file (when rawScriptType=FILE), size 1
     */
    private List<ResourceInfo> resourceList;

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
        if (StringUtils.isBlank(flinkJdbcUrl)) {
            return false;
        }
        if (SCRIPT_SOURCE_FILE.equals(initScriptType)
                && (CollectionUtils.isEmpty(initScriptResourceList) || initScriptResourceList.size() < 1)) {
            return false;
        }
        if (SCRIPT_SOURCE_FILE.equals(rawScriptType)) {
            return CollectionUtils.isNotEmpty(resourceList) && resourceList.size() >= 1;
        }
        return SCRIPT_SOURCE_SCRIPT.equals(rawScriptType) && StringUtils.isNotBlank(rawScript);
    }

    @Override
    public List<ResourceInfo> getResourceFilesList() {
        List<ResourceInfo> list = new java.util.ArrayList<>();
        if (SCRIPT_SOURCE_FILE.equals(initScriptType) && CollectionUtils.isNotEmpty(initScriptResourceList)) {
            list.addAll(initScriptResourceList);
        }
        if (SCRIPT_SOURCE_FILE.equals(rawScriptType) && CollectionUtils.isNotEmpty(resourceList)) {
            list.addAll(resourceList);
        }
        return list;
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

    public String getInitScriptType() {
        return initScriptType;
    }

    public void setInitScriptType(String initScriptType) {
        this.initScriptType = initScriptType;
    }

    public String getInitScript() {
        return initScript;
    }

    public void setInitScript(String initScript) {
        this.initScript = initScript;
    }

    public List<ResourceInfo> getInitScriptResourceList() {
        return initScriptResourceList;
    }

    public void setInitScriptResourceList(List<ResourceInfo> initScriptResourceList) {
        this.initScriptResourceList = initScriptResourceList;
    }

    public String getRawScriptType() {
        return rawScriptType;
    }

    public void setRawScriptType(String rawScriptType) {
        this.rawScriptType = rawScriptType;
    }

    public String getRawScript() {
        return rawScript;
    }

    public void setRawScript(String rawScript) {
        this.rawScript = rawScript;
    }

    public List<ResourceInfo> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<ResourceInfo> resourceList) {
        this.resourceList = resourceList;
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

