/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2012-2-14
 * 
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.threading;

import java.util.concurrent.Executor;

/**
 * <b>描述: </b>该类继承自{@link ObservableExecutor}且间接实现了{@link Executor}接口，且通过父类
 * {@link ObservableExecutor}扩展，使其拥有了仅允许同时执行固定任务量的功能
 * <p>
 * <b>功能: </b>一个具有同时执行固定任务量功能的{@link Executor}接口
 * <p>
 * <b>用法: </b>导出最终为Java标准的{@link Executor}接口，因此参照{@link Executor}接口使用
 * <p>
 *
 * Created by Coral@feinno.com
 * @see Executor
 * @see ObservableExecutor
 */
public class FixedObservableExecutor extends ObservableExecutor implements DetectableExecutor {
	private int workerSize;
	private int queueSize;

	public FixedObservableExecutor(String name, Executor executor, int limit, int size) {
		super(name, executor);
		this.workerSize = size;
		this.queueSize = limit;
		this.getSizeCounter().setRawValue(size);
	}

	@Override
	public void execute(Runnable command) {
		int concurrent = getWorkerCounter().getConcurrent();
		if (concurrent >= queueSize) {
			throw new ExecutorBusyException(ExecutorException.OVER_MAX_CONCURRENT + " queueLimit:" + queueSize, this.getName());
		} else {
			super.execute(command);
		}
	}

	@Override
	public String toString() {
		return String.format("name=%s,queueLimit=%s,counter={%s}", getName(), queueSize, getWorkerCounter());
	}

	@Override
	public boolean isWorkerBusy() {
		return getWorkerCounter().getConcurrent() >= workerSize;
	}

	@Override
	public boolean isQueueBusy() {
		return getWorkerCounter().getConcurrent() >= queueSize;
	}

	@Override
	public int getQueueSize() {
		return 0;
	}
}
