package org.helium.boot.helium.service;


import org.helium.boot.helium.config.CommonConfig;
import org.helium.boot.helium.task.HeliumBootTaskTest;
import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.annotations.TaskEvent;
import org.helium.framework.task.TaskProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
