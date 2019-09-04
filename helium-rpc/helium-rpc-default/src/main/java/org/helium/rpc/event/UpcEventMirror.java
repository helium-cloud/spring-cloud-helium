package org.helium.rpc.event;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author coral
 * @version 创建时间：2015年1月27日
 */
class UpcEventMirror<E extends UpcEvent> implements UpcEvent {

	/**
	 * true : cache1 , false : cache2
	 */
	private AtomicBoolean cacheOff = new AtomicBoolean(false);
	private ConcurrentLinkedQueue<UpcEventListenerFuture<E>> cache1 = new ConcurrentLinkedQueue<>();
	private ConcurrentLinkedQueue<UpcEventListenerFuture<E>> cache2 = new ConcurrentLinkedQueue<>();

	@Override
	public String getEventName() {
		return null;
	}

	public void addListener(UpcEventListenerFuture<E> listener) {
		if (cacheOff.get()) {
			cache1.add(listener);
		} else {
			cache2.add(listener);
		}
	}

	public void remove(UpcEventListenerFuture<E> listener) {
		if (cacheOff.get()) {
			cache1.remove(listener);
		} else {
			cache2.remove(listener);
		}
	}

	/**
	 * 同步调用
	 *
	 * @param event
	 */
	public void fire(E event) {
		if (cacheOff.compareAndSet(true, false)) {// use cache1
			fire(cache1, cache2, event);
		} else if (cacheOff.compareAndSet(false, true)) {// use cache2
			fire(cache2, cache1, event);
		}
	}

	private void fire(ConcurrentLinkedQueue<UpcEventListenerFuture<E>> cacheForm, ConcurrentLinkedQueue<UpcEventListenerFuture<E>> cacheTo, E event) {
		while (!cacheForm.isEmpty()) {
			UpcEventListenerFuture<E> listener = cacheForm.poll();

			if (!listener.isExpire()) {
				listener.fire(event);
				cacheTo.add(listener);
			}
		}
	}

	public int size() {
		return cache1.size() + cache2.size();
	}

}
