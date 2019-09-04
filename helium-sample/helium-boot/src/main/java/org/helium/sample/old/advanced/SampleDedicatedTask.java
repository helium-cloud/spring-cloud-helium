//package org.helium.sample.old.advanced;
//
//import org.helium.framework.annotations.TaskImplementation;
//import org.helium.framework.task.DedicatedTask;
//import org.helium.framework.task.DedicatedTaskContext;
//import org.helium.sample.old.quickstart.SampleUser;
//
///**
// * Created by Coral on 7/13/17.
// */
//@TaskImplementation(event = "quickstart:SampleDedicatedTask")
//public class SampleDedicatedTask implements DedicatedTask<SampleUser> {
//	@Override
//	public void processTask(DedicatedTaskContext ctx, SampleUser args) {
//		// DO SOMETHING
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		Integer i = (Integer)ctx.getSession("count");
//		if (i == null) {
//			i = 1;
//		} else {
//			i = i + 1;
//		}
//
//		ctx.putSession("count", i);
//
//		System.out.println(">>> SampleDedicatedTask: tag=" + args.getTag() + " count=" + i);
//
//		//
//		// DedicatedTask完成运行后必须运行setTaskRunnable()方法设置为可用
//		ctx.setTaskRunnable();
//	}
//
//	@Override
//	public void processTaskRemoved(DedicatedTaskContext ctx) {
//
//	}
//}
//
//
//
//
//
//
//
//
