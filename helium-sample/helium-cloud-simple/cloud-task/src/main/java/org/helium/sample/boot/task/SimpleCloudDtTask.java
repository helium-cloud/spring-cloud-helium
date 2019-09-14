package org.helium.sample.boot.task;


import com.alibaba.fastjson.JSONObject;
import org.helium.cloud.task.annotations.TaskImplementation;
import org.helium.cloud.task.api.DedicatedTask;
import org.helium.cloud.task.api.DedicatedTaskContext;
import org.helium.cloud.task.api.Task;
import org.helium.sample.boot.entity.SimpleDtArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TaskImplementation(event = SimpleCloudDtTask.TAG)
public class SimpleCloudDtTask implements DedicatedTask<SimpleDtArgs> {
    public static final String TAG  ="Task:SimpleCloudDtTask";
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCloudDtTask.class);

	@Override
	public void processTask(DedicatedTaskContext ctx, SimpleDtArgs args) {
		LOGGER.info("SimpleCloudDtTask task exec:{}", JSONObject.toJSONString(args, true));
	}

	@Override
	public void processTaskRemoved(DedicatedTaskContext ctx) {
		LOGGER.info("SimpleCloudDtTask task exec:{}", ctx);
	}
}
