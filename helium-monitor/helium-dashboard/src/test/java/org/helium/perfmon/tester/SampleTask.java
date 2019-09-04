package org.helium.perfmon.tester;

import org.helium.framework.annotations.TaskImplementation;
import org.helium.framework.task.Task;

/**
 * Created by Coral on 10/28/15.
 */
@TaskImplementation(id = "test:SampleTask", event="test:Sample")
public class SampleTask implements Task<String> {
	@Override
	public void processTask(String args) {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
