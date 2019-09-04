package org.helium.threading;

import org.helium.util.Event;
import org.helium.util.EventHandler;
import org.helium.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 *
 * 用以帮助异步调用转为同步调用的工具类<br>
 * 调用此类的await方法后，它会阻塞当前线程，等待处理线程执行完毕，由处理线程将结果返回到当前线程，来解除阻塞.
 * 异步不会触发默认的TimeoutException逻辑
 *
 * @author Lv.Mingwei *
 * @see EventHandler
 * @see FutureCallbackTask
 * @param <V>
 *            类型参数
 */
public class Future<V> {
	private static final Logger LOGGER = LoggerFactory.getLogger(Future.class);

	/**
	 * 创建一个可供直接返回的Future对象
	 * @param args
	 * @param <E>
	 * @return
	 */
	public static <E> Future<E> createResult(E args) {
		Future<E> result = new Future<>();
		result.complete(args);
		return result;
	}

	/** 默认超时时间为90秒 */
	public static final int TIMEOUT = 90 * 1000;

	/** Future的执行结果 */
	private Result<V> result;

	/** 用户可根据需要创建事件完成后监听 */
	private Event<Result<V>> listener;

	/** 内部同步控制器 */
	private final Sync sync;

	/** 是否正在等待 */
	private AtomicBoolean waiting;

	/** 执行回调时的执行器 */
	private Executor executor;

	/** 默认的超时时间: 90秒 */
	private int timeoutMs = TIMEOUT;

	/** 允许由调用代码自由设置的Context */
	private Object contextObject;

	public Future() {
		result = null;
		waiting = new AtomicBoolean(false);
		sync = new Sync();
		listener = new Event<Result<V>>(this);
	}

	/**
	 *
	 * 获取设置的执行器
	 *
	 * @return
	 */
	public Executor getExecutor() {
		return executor;
	}

	/**
	 *
	 * 设置一个执行器, 当设置了一个不为空的执行器后, 会使用这个执行器进行回调操作
	 *
	 * @param executor
	 */
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	/**
	 *
	 * 获取允许自由设置的上下文
	 *
	 * @return
	 */
	public Object getContextObject() {
		return contextObject;
	}

	/**
	 * 设置自由上下文
	 */
	public void setContextObject(Object obj) {
		this.contextObject = obj;
	}

	/**
	 *
	 * 是否完成
	 *
	 * @return
	 */
	public boolean isDone() {
		return result != null;
	}

	/**
	 *
	 * 设置超时的毫秒数
	 *
	 * @param milliseconds
	 */
	public void setTimeout(int milliseconds) {
		timeoutMs = milliseconds;
	}

	/**
	 *
	 * 返回超时时间(毫秒)
	 *
	 * @return
	 */
	public int getTimeout() {
		return timeoutMs;
	}

	/**
	 *
	 * 是否处于等待中
	 *
	 * @return
	 */
	public boolean isWaiting() {
		return waiting.get();
	}

	/**
     * 获得执行结果，请注意，当结果未获取之前，此方法会导致当前线程阻塞，如果在规定时间内未获取到结果，抛出TimeoutException
	 *
	 * @return
	 * @throws Exception
	 */
	public V getValue() throws Exception{
        if (result != null) {
            return result.get();
        }

        if (await()) {
            return result.get();
        } else {
            throw new TimeoutException(String.format("future Timeout ( %sms)", timeoutMs)); // 临时
        }
	}


	/**
	 * 阻塞当前线程直到拿到结果, 使用已设置的超时时间
	 */
	public boolean await() {
		return await(timeoutMs);
	}

	/**
	 *
	 * 阻塞当前线程，等待milliseconds毫秒，如果在时间内完成
	 *
	 * @param milliseconds
	 *            超时毫秒数, -1表示无限等待
	 * @return 在时间内拿到结果返回true, 否则返回false
	 */
	public boolean await(long milliseconds) {
		if (result != null) {
			return true;
		}
		//
		// 如果已经等待一次了, 则仅等待一次
		if (waiting.compareAndSet(false, true)) {
			if (milliseconds == -1) {
				try {
					sync.acquireSharedInterruptibly(1);
					return true;
				} catch (InterruptedException e) {
					throw new RuntimeException(e); //
				} finally {
					waiting.set(false);
				}
			} else {
				try {
					return sync.tryAcquireSharedNanos(1, TimeUnit.MILLISECONDS.toNanos(milliseconds));
				} catch (InterruptedException e) {
					throw new RuntimeException(e); //
				} finally {
					waiting.set(false);
				}
			}
		} else {
			throw new IllegalStateException("Already waiting...");
		}
	}

	/**
	 *
	 * 添加完成的listener，如果此Future已经完成了运行，则直接执行此listener，listener仅能被执行一次
	 *
	 * @param arg
	 */
	public void addListener(final FutureListener<V> arg) {
		boolean runNow = false;
		synchronized (this) {
			if (result != null) {
				//
				// 因为handler只会执行一次, 所以如果已经做完了, 就直接触发handler
				runNow = true;
			} else {
				listener.addListener(new EventHandler<Result<V>>() {
					@Override
					public void run(Object sender, Result<V> e) {
						arg.run(e);
					}
				});
			}
		}

		if (runNow) {
			arg.run(result);
		}
	}

	// /**
	// *
	// * 当回调的时候尝试在当前线程池(SessionExecutor)上续接此操作
	// *
	// * @return
	// */
	// public boolean relaySessionTask() {
	// ThreadContext currentThread = ThreadContext.getCurrent();
	// SessionTask task = (SessionTask) currentThread
	// .getNamedContext(ThreadContextName.SESSION_TASK);
	// if (task != null) {
	// this.sessionTask = task;
	// this.executor = (Executor) currentThread
	// .getNamedContext(ThreadContextName.EXECUTOR);
	// return true;
	// } else {
	// return false;
	// }
	// }

	/**
	 *
	 * 设置结果, 如果有线程处于wait状态, 则使用当前线程解锁wait并执行回调<br>
	 * 当设置了回调线程池时, 使用回调线程池进行回调<br>
	 * 回调的FutureCallback扩展自Runnable, 能够允许在executor中执行更自由的操作<br>
	 *
	 * @param value
	 */
	public void complete(Object value, Exception e) {
		//
		// 标记为完成, 多次调用的话, 只有第一次有效,
		final EventHandler<Result<V>>[] handlers;
		synchronized (this) {
			if (result == null) {
				if (e == null) {
					result = Result.create((V)value);
				} else {
					result = Result.createWithError(e);
				}

				handlers = listener.getAllHandlers();
				//
				// 没有需要执行的直接在锁中释放等待, 避免并发问题
				if (handlers == null || handlers.length == 0) {
					sync.releaseShared(1);
					return;
				}
			} else {
				throw new IllegalStateException("Already complete");
			}
		}

		//
		// 不在等待中,
		sync.releaseShared(1); //

		if (executor == null) {
			//
			// 没有设置线程池, 使用当前线程池执行回调
			runHandlers(handlers);
		} else {
			//
			// 使用设置好的线程池, FutureCallbackTask接口会将Future额外暴露给Executor便于实现一些定制的逻辑
			executor.execute(new FutureCallbackTask() {
				@Override
				public void run() {
					runHandlers(handlers);
				}

				@Override
				public Future<?> getFuture() {
					return Future.this;
				}
			});
		}
	}

	/**
	 * complete(V value, Exception e)方法的重载，任务成功完成时，仅提供Value
	 *
	 * @param value
	 */
	public void complete(V value) {
		this.complete(value, null);
	}

	/**
	 * complete(V value, Exception e)方法的重载，任务失败时，仅提供异常
	 *
	 * @param e
	 */
	public void complete(Exception e) {
		this.complete(null, e);
	}

	/**
	 *
	 * 执行Handlers
	 *
	 * @param handlers
	 */
	private void runHandlers(EventHandler<Result<V>>[] handlers) {
		//
		// List<Exception> errors;
		for (EventHandler<Result<V>> handler : handlers) {
			try {
				handler.run(this, result);
			} catch (Exception ex) {
				LOGGER.error("catch error {}", ex);
			} catch (Error ex) {
				LOGGER.error("catch serius error {}", ex);
				throw ex;
			}
		}
	}

	/**
	 * 同步控制器，目的为实现阻塞，继承自AQS
	 *
	 * @author Lv.Mingwei
	 */
	private final class Sync extends AbstractQueuedSynchronizer {
		private static final long serialVersionUID = -990419035911612884L;

		/** 停止状态 */
		private static final int STOP = 0;

		/** 正在运行中 */
		private static final int RUNNING = 1;

		/**
		 * 初始化为未运行状态
		 */
		Sync() {
			setState(RUNNING);
		}

		/**
		 * AQS的惯用法，此处标识当AQS尝试获取共享锁时，是否获取成功，如果当前不处于运行状态，则可以获取
		 */
		@Override
		public int tryAcquireShared(int acquires) {
			return getState() != RUNNING ? 1 : -1;
		}

		/**
		 * AQS的惯用法，此处标识为释放
		 */
		@Override
		public boolean tryReleaseShared(int releases) {
			for (;;) {
				int c = getState();
				if (c != RUNNING)
					return false;
				if (compareAndSetState(c, STOP)) {
					return true;
				}
			}
		}
	}
}
