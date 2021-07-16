package org.helium.sample.boot.task;


import org.helium.framework.annotations.TaskImplementation;
import org.helium.framework.task.BatchTask;
import org.helium.sample.boot.entity.SimpleArgs;
import org.helium.superpojo.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@TaskImplementation(event = SimpleCloudBatchTask.TAG)
public class SimpleCloudBatchTask implements BatchTask<SimpleArgs> {
    public static final String TAG  ="Task:SimpleCloudBatchTask";
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCloudBatchTask.class);

	@Override
	public void processTask(List<SimpleArgs> argList) {
		LOGGER.info("SimpleCloudBatchTask task exec:{}", JsonUtils.toJson(argList));
	}
}
