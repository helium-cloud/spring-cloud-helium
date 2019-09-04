package org.helium.dtask.tester;

import org.helium.rpc.RpcEndpointFactory;
import org.helium.rpc.client.RpcProxyFactory;
import org.helium.framework.annotations.TaskImplementation;
import org.helium.framework.configuration.Environments;
import org.helium.framework.task.DedicatedTask;
import org.helium.framework.task.DedicatedTaskContext;

/**
 * Created by Coral on 10/28/15.
 */
@TaskImplementation(id = "test:SampleDedicatedTask", event="test:SampleDedicatedTask")
public class SampleDedicatedTask implements DedicatedTask<SampleDedicatedTaskArgs> {
	@Override
	public void processTask(DedicatedTaskContext ctx, SampleDedicatedTaskArgs args) {
		// ctx.putSession("callback", args.getCallbackUrl());
		String marker = (String)ctx.getSession("mark");

		args.setTaskPid(Environments.getPid());

		TaskCollectService service = RpcProxyFactory.getTransparentProxy("test.CollectService", TaskCollectService.class,
				() -> RpcEndpointFactory.parse(args.getCallbackUrl()),0);
		service.taskComplete(args);
		ctx.setTaskRunnable();
	}

	@Override
	public void processTaskRemoved(DedicatedTaskContext ctx) {
	}
}
