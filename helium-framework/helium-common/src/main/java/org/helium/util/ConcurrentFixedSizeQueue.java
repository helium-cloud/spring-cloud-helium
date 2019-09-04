package org.helium.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 支持并发且固定大小的一个Queue<br>
 * 这个类是{@link ConcurrentLinkedQueue}类的一个装饰类，目的是弥补 {@link ConcurrentLinkedQueue}
 * 类取队列数量时需要遍历整个队列的劣势，并且增加了一个固定容量的限制，超过此容量添加方法会返回<code>false</code>
 * ,用以防止内存溢出，默认的容量是<code>Integer.MAX_VALUE</code><br>
 * {@link ConcurrentLinkedQueue}类<code>size()</code>方法在取数量时是一个线性计数操作，因此效率低，可详见
 * {@link ConcurrentLinkedQueue#size()}
 * 
 * @author Lv.Mingwei
 * 
 */
public class ConcurrentFixedSizeQueue<E> implements Queue<E> {

	// 支持并发的一个队列
	private ConcurrentLinkedQueue<E> queue = new ConcurrentLinkedQueue<E>();

	// 当前队列的的大小
	private AtomicInteger size = new AtomicInteger(0);

	// 队列的默认容量
	private int cacheSize = Integer.MAX_VALUE;

	// 提供计数功能的一个支持并发的整数类型
	public static AtomicInteger counter = new AtomicInteger();

	public ConcurrentFixedSizeQueue() {
	}

	public ConcurrentFixedSizeQueue(int cacheSize) {
		this.cacheSize = cacheSize;
	}

	@Override
	public int size() {
		return size.get();
	}

	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public boolean contains(Object obj) {
		return queue.contains(obj);
	}

	@Override
	public Iterator<E> iterator() {
		return queue.iterator();
	}

	@Override
	public Object[] toArray() {
		return queue.toArray();
	}

	@Override
	public boolean remove(Object obj) {
		if (queue.remove(obj)) {
			size.decrementAndGet();
			return true;
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		return queue.containsAll(collection);
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {
		if (size.get() + collection.size() < cacheSize && queue.addAll(collection)) {
			size.getAndAdd(collection.size());
			return true;
		}
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		if (queue.removeAll(collection)) {
			// 很可惜， AtomicInteger 好像没有减少指定值的方法，只能自己通过循环来解决了
			for (@SuppressWarnings("unused")
			Object object : collection) {
				size.decrementAndGet();
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		// 因为此方法涉及到的大小计算较多，因此不支持
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		queue.clear();
		size.set(0);
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return queue.toArray(a);
	}

	@Override
	public boolean add(E e) {
		if (size.get() < cacheSize && queue.add(e)) {
			size.incrementAndGet();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public E remove() {
		E e;
		try {
			e = queue.remove();
			size.decrementAndGet();
		} catch (NoSuchElementException exception) {
			throw exception;
		}
		return e;
	}

	@Override
	public E poll() {
		E e = queue.poll();
		if (e != null) {
			size.decrementAndGet();
		}
		return e;
	}

	@Override
	public E element() {
		return queue.element();
	}

	@Override
	public boolean offer(E e) {
		if (size.get() < cacheSize && queue.offer(e)) {
			size.incrementAndGet();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public E peek() {
		return queue.peek();
	}

	@Override
	public String toString() {
		return queue.toString();
	}
}
