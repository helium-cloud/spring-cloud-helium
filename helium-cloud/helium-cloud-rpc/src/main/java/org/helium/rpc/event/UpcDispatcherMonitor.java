package org.helium.rpc.event;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * @author coral
 * @version 创建时间：2015年1月28日 类说明
 */
public class UpcDispatcherMonitor {

	static UpcDispatcherMonitor INSTANCE = new UpcDispatcherMonitor();

	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private Timer timer;
	private Map<String, UpcEventDispatcher<?>> mapping = new ConcurrentHashMap<>();

	private AtomicInteger off = new AtomicInteger();

	UpcDispatcherMonitor() {
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				WriteLock writeLock = lock.writeLock();
				writeLock.lock();
				try {
					Collection<UpcEventDispatcher<?>> values = mapping.values();
					Iterator<UpcEventDispatcher<?>> iterator = values.iterator();

					while (iterator.hasNext()) {
						UpcEventDispatcher<?> dispatcher = iterator.next();
						if (off.compareAndSet(2, 0)) {
							dispatcher.monitor(11);
						}
						dispatcher.monitor(10);
						off.incrementAndGet();
					}
				} finally {
					writeLock.unlock();
				}
			}
		}, 10 * 1000, 10 * 1000);

	}

	public void put(String name, UpcEventDispatcher<?> dispatcher) {
		ReadLock readLock = lock.readLock();
		readLock.lock();
		try {
			mapping.put(name, dispatcher);
		} finally {
			readLock.unlock();
		}
	}
}
