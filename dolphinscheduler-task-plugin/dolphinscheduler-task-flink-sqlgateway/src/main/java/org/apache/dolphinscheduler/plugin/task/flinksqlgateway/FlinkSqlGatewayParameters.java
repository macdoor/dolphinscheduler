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

    /** Main script from inline SQL string */
    public static final String RAW_SCRIPT_TYPE_SCRIPT = "SCRIPT";
    /** Main script from resource center file */
    public static final String RAW_SCRIPT_TYPE_FILE = "FILE";

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
     * main script type: SCRIPT = inline sql string, FILE = resource center file
     */
    private String rawScriptType = RAW_SCRIPT_TYPE_SCRIPT;

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
        if (RAW_SCRIPT_TYPE_FILE.equals(rawScriptType)) {
            return CollectionUtils.isNotEmpty(resourceList) && resourceList.size() >= 1;
        }
        return RAW_SCRIPT_TYPE_SCRIPT.equals(rawScriptType) && StringUtils.isNotBlank(rawScript);
    }

    @Override
    public List<ResourceInfo> getResourceFilesList() {
        if (RAW_SCRIPT_TYPE_FILE.equals(rawScriptType) && CollectionUtils.isNotEmpty(resourceList)) {
            return resourceList;
        }
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

