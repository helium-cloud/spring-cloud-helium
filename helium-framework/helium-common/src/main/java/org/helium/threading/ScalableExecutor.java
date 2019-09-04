package org.helium.threading;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/***
 * <p>
 * 在<tt>ThreadPoolExecutor</tt>基础上增加了 pool的可伸缩性 线程池 启动后 迅速增长到最小值corePoolSize,
 * 如果corePoolSize不满足需要 最大可以增加到maximumPoolSize, 如果还不够用, task被暂时存在FIFO queue中等待执行.
 * maxTaskQueueCapacity 是可以暂存的最大值, 超过则 throw RejectedExecutionException. 任务量下降,
 * 池的大小可以收缩至 corePoolSize
 * 
 * @author linsu@feinno.com 3/27/2012
 */
public class ScalableExecutor extends ThreadPoolExecutor
{
	public ScalableExecutor()
	{
		this(1000);
	}

	public ScalableExecutor(int maxTaskQueueCapacity)
	{
		this(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * 5,
				maxTaskQueueCapacity);
	}

	public ScalableExecutor(int corePoolSize, int maximumPoolSize, int maxTaskQueueCapacity)
	{
		super(corePoolSize, maximumPoolSize, 60L, TimeUnit.SECONDS, new ThreadPoolBlockingQueue<Runnable>(
				maxTaskQueueCapacity));
		this.setRejectedExecutionHandler(new RejectedExecutionHandler() {
			ThreadPoolBlockingQueue<Runnable> taskQueue = (ThreadPoolBlockingQueue<Runnable>) ScalableExecutor.this
					.getQueue();

			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
			{
				if (taskQueue.OfferAfterMaxThread(r) == false)
					throw new RejectedExecutionException("threadpool is full");
			}
		});
	}
}

class ThreadPoolBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>
{
	private SynchronousQueue<E> synchronousQueue;
	private LinkedList<E> innerQueue;
	private ReentrantLock innerQueueLock;
	private final int capacity;
	private int count;

	public ThreadPoolBlockingQueue(int maxTaskQueueCapacity)
	{
		this.capacity = (maxTaskQueueCapacity < 0) ? 0 : maxTaskQueueCapacity;
		synchronousQueue = new SynchronousQueue<E>();
		innerQueue = new LinkedList<E>();
		innerQueueLock = new ReentrantLock();
		count = 0;
	}

	@Override
	public E poll()
	{
		E e = synchronousQueue.poll();
		return (e == null) ? innerPoll() : e;
	}

	@Override
	public boolean remove(Object o)
	{
		boolean r = synchronousQueue.remove(o);
		if (r != true) {
			innerQueueLock.lock();
			try {
				if (innerQueue.remove(o))
					count--;
			} finally {
				innerQueueLock.unlock();
			}
		}
		return r;
	}

	@Override
	public E peek()
	{
		return null;
	}

	@Override
	public boolean offer(E e)
	{
		return synchronousQueue.offer(e);
	}

	boolean OfferAfterMaxThread(E e)
	{
		innerQueueLock.lock();
		try {
			if (count < capacity) {
				innerQueue.offer(e);
				count++;
				return true;
			} else {
				return false;
			}
		} finally {
			innerQueueLock.unlock();
		}
	}

	@Override
	public void put(E e) throws InterruptedException
	{
	}

	@Override
	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException
	{
		return false;
	}

	private E innerPoll()
	{
		E e = null;
		innerQueueLock.lock();
		try {
			if (count > 0) {
				e = innerQueue.poll();
				count--;
			}
		} finally {
			innerQueueLock.unlock();
		}
		return e;
	}

	@Override
	public E take() throws InterruptedException
	{
		E e = innerPoll();
		return (e == null) ? synchronousQueue.take() : e;
	}

	@Override
	public E poll(long timeout, TimeUnit unit) throws InterruptedException
	{
		E e = innerPoll();
		return (e == null) ? synchronousQueue.poll(timeout, unit) : e;
	}

	@Override
	public int remainingCapacity()
	{
		return 0;
	}

	/**
	 * innerQueue与synchronousQueue合并输出
	 */
	@Override
	public int drainTo(Collection<? super E> c)
	{
		int count = synchronousQueue.drainTo(c);
		Iterator<E> iterator = innerQueue.iterator();
		while (iterator.hasNext()) {
			count++;
			c.add(iterator.next());
		}
		return count;
	}

	@Override
	public int drainTo(Collection<? super E> c, int maxElements)
	{
		return 0;
	}

	@Override
	public Iterator<E> iterator()
	{
		return null;
	}

	@Override
	public int size()
	{
		return count;
	}
}