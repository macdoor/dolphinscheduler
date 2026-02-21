package org.apache.dolphinscheduler.plugin.task.flinksqlgateway;

import org.apache.dolphinscheduler.plugin.task.api.TaskChannel;
import org.apache.dolphinscheduler.plugin.task.api.TaskChannelFactory;

import com.google.auto.service.AutoService;

@AutoService(TaskChannelFactory.class)
public class FlinkSqlGatewayTaskChannelFactory implements TaskChannelFactory {

    @Override
    public TaskChannel create() {
        return new FlinkSqlGatewayTaskChannel();
    }

    @Override
    public String getName() {
        return "FLINK_SQLGATEWAY";
    }
}

