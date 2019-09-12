package org.helium.sample.boot.control;


import org.helium.cloud.task.annotations.TaskEvent;
import org.helium.cloud.task.api.TaskProducer;
import org.helium.sample.boot.task.SimpleCloudTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class TaskManagerControl {


	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCloudTask.class);

    @TaskEvent(SimpleCloudTask.TAG)
    private TaskProducer<String> simpleCloudTask;


    @RequestMapping(value={"/simple"})
    public String simple(){
    	String uuid = UUID.randomUUID().toString();
		LOGGER.info("simple task producer:{}", uuid);
		simpleCloudTask.produce(uuid);
        return "simple task";
    }
}