package org.helium.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * 用于入Queue, 轮询, 执行的工具类
 * Created by Coral on 11/12/15.
 */
public class SimpleQueuedWorker<E> {
	private int capacity = 4096;
	private String name;
	private Thread thread;
	private Queue<E> queue;
	private Logger logger;
	private Consumer<E> consumer;

	public SimpleQueuedWorker(String name, Consumer<E> consumer) {
		this.name = name;
		this.consumer = consumer;
		this.logger = LoggerFactory.getLogger("SimpleQueuedWorker." + name);
		this.queue = new LinkedList<>();

		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				SimpleQueuedWorker.this.run();
			}
		});
		thread.setDaemon(true);
		thread.setName("SimpleQueuedWorker:" + name);
		thread.start();
	}

	public String getName() {
		return name;
	}

	public void enqueue(E e) {
		synchronized (this) {
			if (queue.size() >= capacity) {
				logger.error("exceed queue capacity: {}" + capacity);
			} else {
				queue.add(e);
			}
		}
	}

	private void run() {
		while (true) {
			try {
				E a = null;
				synchronized (this) {
					if (!queue.isEmpty()) {
						a = queue.poll();
					}
				}
				if (a != null) {
					consumer.accept(a);
				} else {
					Thread.sleep(50);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception ex) {
				logger.error("run consumer failed: {}", ex);
			}
		}
	}
}
