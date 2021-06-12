package org.helium.sample.boot.control;


import org.helium.framework.annotations.TaskEvent;
import org.helium.framework.task.TaskProducer;
import org.helium.sample.boot.entity.SimpleArgs;
import org.helium.sample.boot.entity.SimpleDtArgs;
import org.helium.sample.boot.task.SimpleCloudBatchTask;
import org.helium.sample.boot.task.SimpleCloudTask;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class TaskManagerControl {


	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCloudTask.class);

    @TaskEvent(SimpleCloudTask.TAG)
    private TaskProducer<SimpleArgs> simpleCloudTask;
//
//	@TaskEvent(SimpleCloudDtTask.TAG)
//	private TaskProducer<SimpleDtArgs> simpleCloudDtTask;


	@TaskEvent(SimpleCloudBatchTask.TAG)
	private TaskProducer<SimpleArgs> simpleCloudBatchTask;

    @RequestMapping(value={"/st"})
    public String st(){
    	String uuid = UUID.randomUUID().toString();
		SimpleArgs sa = new SimpleArgs();
		sa.setUser(uuid);
		LOGGER.info("simpleCloudTask task producer:{}", uuid);
		simpleCloudTask.produce(sa);
        return "simple task";
    }

	@RequestMapping(value={"/dt/{userid}"})
	public String dt(@PathVariable String userid){

		String uuid = UUID.randomUUID().toString();
		SimpleDtArgs simpleDtArgs = new SimpleDtArgs();
		if (!StringUtils.isNullOrEmpty(userid)){
			uuid = userid;
		}
		simpleDtArgs.setUser(uuid);
//		LOGGER.info("simpleCloudDtTask task producer:{}", uuid);
//		simpleCloudDtTask.produce(simpleDtArgs);
		return "simple task";
	}


	@RequestMapping(value={"/bt"})
	public String bt(){
		String uuid = UUID.randomUUID().toString();
		SimpleArgs sa = new SimpleArgs();
		sa.setUser(uuid);
		LOGGER.info("simpleCloudBatchTask task producer:{}", uuid);
		simpleCloudBatchTask.produce(sa);
		return "simple task";
	}
}