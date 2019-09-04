package org.helium.threading;

import org.helium.util.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于Hash进行任务分派的消费队列
 * <p>
 * 此Queue的特性是可以将同类的任务分配给固定的线程去执行，从而实现同类任务的串行处理，异类任务的并行处理
 * 
 * @author Lv.Mingwei
 * 
 * @param <E>
 */
public class HashDispatchTaskQueue<E extends HashDispatchElement> implements Queue<E> {

	/** 消费线程池的大小 */
	private int poolSize;

	/** 当前队列的最高存储限制 */
	private int queueLimit;

	/** 线程池 */
	private Thread[] threadPool;

	/** 任务队列池 */
	private BlockingQueue<E>[] queuePool;

	/** 任务回调方法 */
	private Action<E> action;

	/** Queue计数器 */
	private AtomicInteger counter;

	/** 是否运行标识 */
	private AtomicBoolean isRun;

	/** 日志引用 */
	private static final Logger LOGGER = LoggerFactory.getLogger(HashDispatchTaskQueue.class);

	/**
	 * HashDispatchTaskQueue构造方法
	 * 
	 * @param poolSize
	 *            消费线程池大小
	 * @param queueLimit
	 *            queue最大任务存储限制
	 * @param action
	 *            任务回调
	 */
	@SuppressWarnings("unchecked")
	public HashDispatchTaskQueue(int poolSize, int queueLimit, Action<E> action) {
		this.poolSize = poolSize;
		this.queueLimit = queueLimit;
		this.action = action;
		counter = new AtomicInteger(0);
		threadPool = new Thread[poolSize];
		queuePool = new LinkedBlockingQueue[poolSize];
		for (int i = 0; i < poolSize; i++) {
			initialThread(i);
		}
		isRun = new AtomicBoolean(true);
	}

	/**
	 * 初始化线程资源
	 * 
	 * @param index
	 */
	private void initialThread(final int index) {
		queuePool[index] = new LinkedBlockingQueue<>();
		threadPool[index] = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						E e = queuePool[index].take();
						counter.decrementAndGet();
						action.run(e);
					}
				} catch (InterruptedException e) {
					LOGGER.warn("", e);
				}
			}
		});
		threadPool[index].setName("HashDispatchTaskQueue-Pool-" + index);
		threadPool[index].setDaemon(true);
		threadPool[index].start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#size()
	 */
	@Override
	public int size() {
		return counter.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return counter.get() == 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		for (BlockingQueue<E> queue : queuePool) {
			if (queue.contains(o)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#toArray()
	 */
	@Override
	public Object[] toArray() {
		Object[][] objs = new Object[poolSize][];
		int counter = 0;
		int total = 0;
		for (BlockingQueue<E> queue : queuePool) {
			objs[counter] = queue.toArray();
			total += objs[counter].length;
			counter++;
		}
		counter = 0;
		Object[] retval = new Object[total];
		for (Object[] obj : objs) {
			for (Object tmp : obj) {
				retval[counter++] = tmp;
			}
		}
		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#toArray(T[])
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		Object[] objs = toArray();
		for (int i = 0; i < a.length; i++) {
			a[i] = (T) objs[i];
		}
		return a;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		for (BlockingQueue<E> queue : queuePool) {
			if (queue.remove(o)) {
				counter.decrementAndGet();
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object obj : c) {
			if (!contains(obj)) {
				return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		for (E obj : c) {
			if (!add(obj)) {
				return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean isSuccess = true;
		for (Object obj : c) {
			if (remove(obj)) {
				counter.decrementAndGet();
			} else {
				isSuccess = false;
			}
		}
		return isSuccess;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		List<Object> list = Arrays.asList(toArray());
		return list.retainAll(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#clear()
	 */
	@Override
	public void clear() {
		for (BlockingQueue<E> queue : queuePool) {
			queue.clear();
		}
		counter.set(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#iterator()
	 */
	@Override
	public Iterator<E> iterator() {
		Collection<E> tmp = new ArrayList<>();
		for (BlockingQueue<E> queue : queuePool) {
			tmp.addAll(queue);
		}
		return tmp.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Queue#add(java.lang.Object)
	 */
	@Override
	public boolean add(E e) {
		if (offer(e)) {
			return true;
		}
		// 检测，无法添加，例如满了，抛IllegalStateException异常
		throw new IllegalStateException("HashDispatchTaskQueue is full.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Queue#offer(java.lang.Object)
	 */
	@Override
	public boolean offer(E e) {
		if (counter.get() > queueLimit) {
			// 检测，如果满了，返回false
			return false;
		}
		counter.incrementAndGet();
		// 取摩后放入对应的队列
		int index = Math.abs(e.getKey().hashCode() % poolSize);
		return queuePool[index].offer(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Queue#remove()
	 */
	@Override
	public E remove() {
		E retval = poll();
		if (retval == null) {
			throw new NoSuchElementException();
		}
		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Queue#poll()
	 */
	@Override
	public E poll() {
		E retval = null;
		for (BlockingQueue<E> queue : queuePool) {
			// 如果找到可移除的元素，则返回此元素，否则继续寻找
			retval = queue.poll();
			if (retval != null) {
				counter.decrementAndGet();
				return retval;
			}
		}
		// 没有找到，则返回null，此为与remove的区别
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Queue#element()
	 */
	@Override
	public E element() {
		E retval = peek();
		if (retval == null) {
			throw new NoSuchElementException();
		}
		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Queue#peek()
	 */
	@Override
	public E peek() {
		E retval = null;
		for (BlockingQueue<E> queue : queuePool) {
			// 如果找到可获取的元素，则返回此元素，否则继续寻找
			retval = queue.peek();
			if (retval != null) {
				return retval;
			}
		}
		// 没有找到，则返回null，此为与element的区别
		return null;
	}

	/**
	 * 关闭当前任务队列
	 */
	public void close() {
		if (isRun.compareAndSet(true, false)) {
			poolSize = 0;
			queueLimit = 0;
			counter.set(0);
			action = null;
			// 中断任务线程
			for (Thread thread : threadPool) {
				thread.interrupt();
			}
			threadPool = null;
			// 清空queue
			for (BlockingQueue<E> queue : queuePool) {
				queue.clear();
			}
			queuePool = null;
		} else {
			throw new RuntimeException("HashDispatchTaskQueue is already destory.");
		}

	}
}
