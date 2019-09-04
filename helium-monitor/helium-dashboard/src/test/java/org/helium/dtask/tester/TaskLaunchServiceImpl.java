package org.helium.dtask.tester;

import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.annotations.TaskEvent;
import org.helium.framework.configuration.Environments;
import org.helium.framework.spi.task.DedicatedTaskFactory;
import org.helium.framework.task.DedicatedTaskContext;
import org.helium.framework.task.TaskProducer;

/**
 * Created by Coral on 3/19/16.
 */
@ServiceImplementation
public class TaskLaunchServiceImpl implements TaskLaunchService {
	@TaskEvent("test:SampleDedicatedTask")
	private TaskProducer<SampleDedicatedTaskArgs> task;

	@Override
	public void fireTask(SampleDedicatedTaskArgs args) {
		if (args.isCallPutTask()) {
			DedicatedTaskContext ctx = DedicatedTaskFactory.putTaskContext(args.getTag());
			ctx.putSession("callback", args.getCallbackUrl());
		}
		args.setServicePid(Environments.getPid());
		task.produce(args);
	}
}
