package org.helium.sample.boot.task;


import com.alibaba.fastjson.JSONObject;
import org.helium.cloud.task.annotations.TaskImplementation;
import org.helium.cloud.task.api.BatchTask;
import org.helium.cloud.task.api.Task;
import org.helium.sample.boot.entity.SimpleArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@TaskImplementation(event = SimpleCloudBatchTask.TAG)
public class SimpleCloudBatchTask implements BatchTask<SimpleArgs> {
    public static final String TAG  ="Task:SimpleCloudBatchTask";
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCloudBatchTask.class);

	@Override
	public void processTask(List<SimpleArgs> argList) {
		LOGGER.info("SimpleCloudBatchTask task exec:{}", JSONObject.toJSONString(argList, true));
	}
}
