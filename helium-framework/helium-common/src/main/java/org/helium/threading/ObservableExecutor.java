/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2012-2-14
 * 
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.threading;

import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.perfmon.Stopwatch;
import org.helium.perfmon.spi.NumberCounter;
import org.helium.perfmon.spi.TransactionCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * 
 * <b>描述: </b>这是一个{@link Executor}的包装类，使用装饰者模式将{@link Executor}装饰起来，为其增加计数器功能
 * <p>
 * <b>功能: </b>使用装饰者模式将{@link Executor}装饰起来，为其增加计数器功能
 * <p>
 * <b>用法: </b>该类用于继承使用
 * <p>
 * 
 * Created by Coral
 */
public class ObservableExecutor implements Executor {
	private String name;
	private Executor innerExecutor;
	private NumberCounter sizeCounter;
	private TransactionCounter workerCounter;
	private Logger logger;

	public String getName() {
		return name;
	}

	protected TransactionCounter getWorkerCounter() {
		return workerCounter;
	}

	protected NumberCounter getSizeCounter() {
		return sizeCounter;
	}

	protected Logger getLogger() {
		return logger;
	}

	public ObservableExecutor(String name, Executor executor) {
		this.name = name;
		this.innerExecutor = executor;
		ExecutorCounterCategory c = PerformanceCounterFactory.getCounters(ExecutorCounterCategory.class, name);

		this.sizeCounter = (NumberCounter) c.getSizeCounter();
		this.workerCounter = (TransactionCounter) c.getWorkerCounter();
		this.logger = LoggerFactory.getLogger("org.helium.threading.ThreadPool." + name);
	}

	@Override
	public void execute(final Runnable command) {
		final Stopwatch watch = workerCounter.begin();
		innerExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					command.run();
					watch.end();
				} catch (Exception ex) {
					watch.fail(ex);
					logger.error("ThreadPool Error", ex);
				} catch (Error ex) {
					watch.fail(ex);
					logger.error("ThreadPool Exception", ex);
				}
			}
		});
	}
}