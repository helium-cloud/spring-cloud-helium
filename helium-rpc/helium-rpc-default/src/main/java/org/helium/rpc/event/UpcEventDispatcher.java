package org.helium.rpc.event;

import org.helium.threading.ExecutorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Upc 事件 分发器
 *
 * @param <E> 单线程处理消息分发与监听订阅
 * @author coral
 * @version 创建时间：2015年1月27日
 */
public class UpcEventDispatcher<E extends UpcEvent> implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(UpcEventDispatcher.class);
	private String name;
	/**
	 * 分发器运行状态
	 */
	private AtomicBoolean isStart = new AtomicBoolean(false);
	/**
	 * 任务执行线程,负责处理消息分发与挂载监听
	 */
	private Thread thread;
	/**
	 * 任务队列 Task类型包括触发,挂载
	 */
	private LinkedBlockingQueue<Task<E>> selectionKey = new LinkedBlockingQueue<>();
	/**
	 * 监听器存储
	 */
	private ConcurrentHashMap<String, UpcEventMirror<E>> eventMirror = new ConcurrentHashMap<>();

	/**
	 * 回调线程池
	 */
	private Executor executor;
	/**
	 * 超时时间,当分发器处于单次触发时有效
	 */
	private long expireTime = 10 * 1000;
	/**
	 * 监听器类型(单一触发,重复触发)
	 */
	private boolean repeat;

	public UpcEventDispatcher(String name, int size, int limit) {
		this(name, ExecutorFactory.newFixedExecutor("event dispatcher", size, limit));
	}

	public UpcEventDispatcher(String name, int size, int limit, boolean repeat) {
		this(name, ExecutorFactory.newFixedExecutor("event dispatcher", size, limit), repeat);
	}

	public UpcEventDispatcher(String name, Executor executor) {
		this(name, executor, false);
	}

	public UpcEventDispatcher(String name, Executor executor, boolean repeat) {
		this.name = name;
		this.executor = executor;
		this.repeat = repeat;
		UpcDispatcherMonitor.INSTANCE.put(name, this);
	}

	@Override
	public void run() {
		while (true) {
			try {
				Task<E> task = selectionKey.take();
				if (task.type == 1) {// event
					E p = task.e;
					UpcEventMirror<E> mirror = eventMirror.remove(p.getEventName());
					if (mirror != null) {
						mirror.fire(p);

						if (repeat) {
							if (mirror.size() > 0) {
								eventMirror.put(p.getEventName(), mirror);
							}
						}
					}

				} else if (task.type == 2) {// listener
					String key = task.key;
					UpcEventListenerFuture<E> listener = task.listener;

					UpcEventMirror<E> mirror = eventMirror.get(key);
					if (mirror == null) {
						mirror = new UpcEventMirror<>();
						eventMirror.put(key, mirror);
					}
					listener.setMirror(mirror);
					mirror.addListener(listener);
				} else if (task.type == 10) {
					if (LOGGER.isInfoEnabled()) {
						LOGGER.info("{} Event Listener has {}", name, eventMirror.size());

					}
				} else if (task.type == 11) {
					List<String> removeKey = new ArrayList<>();
					Enumeration<String> keys = eventMirror.keys();
					while (keys.hasMoreElements()) {
						String string = keys.nextElement();
						if (eventMirror.get(string).size() == 0) {
							removeKey.add(string);
						}
					}
					for (String key : removeKey) {
						eventMirror.remove(key);
					}

				}
			} catch (InterruptedException e) {
				LOGGER.error("Execute Task Failed.", e);
			}
		}
	}

	public UpcEventListenerFuture<E> addEventListener(String key, UpcEventListener<E> listener) {
		UpcEventListenerFuture<E> future = null;
		if (isStart.get()) {
			future = new UpcEventListenerFuture<E>(executor, listener, repeat, expireTime);
			selectionKey.add(new Task<E>(key, future));
		}
		return future;
	}

	public void fire(E e) {
		if (isStart.get())
			selectionKey.add(new Task<E>(e));
	}

	void monitor(int num) {
		if (isStart.get())
			selectionKey.add(new Task<E>(num));
	}

	public void start() {
		if (isStart.compareAndSet(false, true)) {
			thread = new Thread(this);
			thread.setDaemon(true);
			thread.start();
		}
	}

	public long getExpireTime() {
		return expireTime;
	}

	/**
	 * 秒
	 *
	 * @param expireTime
	 */
	public void setExpireTime(int expireTime) {
		this.expireTime = expireTime * 1000;
	}

	private static class Task<E extends UpcEvent> {
		public Task(int num) {
			type = num;
		}

		public Task(E e) {
			type = 1;
			this.e = e;
		}

		public Task(String key, UpcEventListenerFuture<E> listener) {
			type = 2;
			this.key = key;
			this.listener = listener;
		}

		int type;
		String key;
		E e;
		UpcEventListenerFuture<E> listener;
	}

}
