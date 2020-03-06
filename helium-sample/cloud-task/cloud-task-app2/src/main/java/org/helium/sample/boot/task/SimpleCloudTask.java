package org.helium.sample.boot.task;


import com.alibaba.fastjson.JSONObject;
import org.helium.framework.annotations.TaskImplementation;
import org.helium.framework.task.Task;
import org.helium.sample.boot.entity.SimpleArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TaskImplementation(event = SimpleCloudTask.TAG)
public class SimpleCloudTask implements Task<SimpleArgs> {
    public static final String TAG  ="Task:SimpleCloudTask";
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCloudTask.class);

	@Override
	public void processTask(SimpleArgs args) {
		LOGGER.info("SimpleCloudTask task exec:{}", JSONObject.toJSONString(args, true));
	}
}
