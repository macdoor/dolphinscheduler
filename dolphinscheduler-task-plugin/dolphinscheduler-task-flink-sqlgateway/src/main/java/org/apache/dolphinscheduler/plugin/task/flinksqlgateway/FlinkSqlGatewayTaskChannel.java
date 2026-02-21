package org.apache.dolphinscheduler.plugin.task.flinksqlgateway;

import org.apache.dolphinscheduler.common.utils.JSONUtils;
import org.apache.dolphinscheduler.plugin.task.api.TaskChannel;
import org.apache.dolphinscheduler.plugin.task.api.TaskExecutionContext;
import org.apache.dolphinscheduler.plugin.task.api.parameters.AbstractParameters;

public class FlinkSqlGatewayTaskChannel implements TaskChannel {

    @Override
    public FlinkSqlGatewayTask createTask(TaskExecutionContext taskRequest) {
        return new FlinkSqlGatewayTask(taskRequest);
    }

    @Override
    public AbstractParameters parseParameters(String taskParams) {
        return JSONUtils.parseObject(taskParams, FlinkSqlGatewayParameters.class);
    }
}

