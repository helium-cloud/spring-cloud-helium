package org.helium.sample.boot.service;


import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.annotations.TaskEvent;
import org.helium.framework.task.TaskProducer;
import org.helium.sample.boot.config.CommonConfig;
import org.helium.sample.boot.task.HeliumBootTaskTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@ServiceImplementation
public class HeliumServiceTestImpl implements HeliumServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeliumServiceTestImpl.class);
    @TaskEvent(HeliumBootTaskTest.TAG)
    private TaskProducer<String> uBootTask;

    @Autowired
	private CommonConfig config;

    @Override
    public void test() {
		System.out.println(config);
        uBootTask.produce("test args");
        LOGGER.warn("Test-HeliumServiceTestImpl");
    }
}
