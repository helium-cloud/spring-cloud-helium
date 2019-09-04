package org.helium.perfmon.tester;

import org.helium.dtask.tester.SampleDedicatedTaskArgs;
import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.annotations.TaskEvent;
import org.helium.framework.task.TaskProducer;

/**
 * Created by Coral on 10/28/15.
 */
@ServiceImplementation
public class SampleServiceImpl implements SampleService {
	@TaskEvent("test:DedicatedTask")
	private TaskProducer<SampleDedicatedTaskArgs> taskProducer;

	@Override
	public void testDedicatedTask(long i) {
		// taskProducer.produce("hahaha");
	}
}
