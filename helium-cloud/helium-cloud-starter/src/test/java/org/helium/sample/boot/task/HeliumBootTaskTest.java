package org.helium.sample.boot.task;


import org.helium.framework.annotations.TaskImplementation;
import org.helium.framework.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TaskImplementation(event = HeliumBootTaskTest.TAG)
public class HeliumBootTaskTest implements Task<String> {
    public static final String TAG  ="test:worktest";
    private static final Logger LOGGER = LoggerFactory.getLogger(HeliumBootTaskTest.class);
    @Override
    public void processTask(String args) {
        LOGGER.info("HeliumBootTaskTest task:{}", args);
    }
}
