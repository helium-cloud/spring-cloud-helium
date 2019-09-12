package org.helium.sample.boot.task;


import org.helium.cloud.task.annotations.TaskImplementation;
import org.helium.cloud.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TaskImplementation(event = SimpleCloudTask.TAG)
public class SimpleCloudTask implements Task<String> {
    public static final String TAG  ="Task:SimpleCloudTask";
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCloudTask.class);
    @Override
    public void processTask(String args) {
        LOGGER.info("simple task exec:{}", args);
    }
}
