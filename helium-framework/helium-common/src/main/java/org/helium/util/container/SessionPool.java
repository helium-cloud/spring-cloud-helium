/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2012-2-21
 * 
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.util.container;

import org.helium.util.NumberUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

/**
 * 
 * <b>描述: </b>朴实无华的SessionPool，不使用lock-free方法，优点是内存占用小<br>
 * 这是一个具有指定大小的Session容器，当容器中的Session数量达到限制{@link SessionPool#capacity}，再试图添加
 * {@link SessionPool#add(Object)}时会返回-1，代表添加失败，此类与{@link SessionContext}
 * 联合使用，实现会话池的功能
 * <p>
 * <b>功能: </b>具有指定大小的Session容器，与{@link SessionContext}联合使用，实现会话池的功能
 * <p>
 * <b>用法: </b>
 * 
 * <pre>
 * 一下示例为简单的对{@link SessionPool}进行多线程情况下的增删，如果想了解关于会话的整体控制，请参见{@link SessionContext}
 * final SessionPool&lt;String&gt; sessions = new SessionPool(TEST_SIZE);
 * Executor executor = Executors.newFixedThreadPool(8);
 * final AtomicInteger aint = new AtomicInteger();
 * for (int i = 0; i &lt; TEST_SIZE * 100; i++) {
 * 	final String msg = &quot;foo&quot; + i;
 * 	executor.execute(new Runnable() {
 * 		&#064;Override
 * 		public void run() {
 * 			int n = sessions.add(msg);
 * 			Assert.assertEquals(&quot;session:&quot; + msg, true, n &gt; 0);
 * 			String msg2 = sessions.remove(n);
 * 			Assert.assertEquals(&quot;session:&quot; + msg, msg, msg2);
 * 			aint.incrementAndGet();
 * 		}
 * 	});
 * }
 * Thread.sleep(1000);
 * Assert.assertEquals(TEST_SIZE * 100, aint.get());
 * </pre>
 * 
 * <p>
 * 
 * Created by Coral
 * @see
 * @param <E>
 */
public class SessionPool<E>
{
	private static final int MAX_CAPACITY = 256 * 1024;
	private static final int MAGNIFY = 1024;

	private int head;
	private int mask;
	private int capacity;
	private Object syncRoot;
	private Map<Integer, E> sessions;

	public SessionPool()
	{
		this(MAX_CAPACITY);
	}

	public SessionPool(int capacity)
	{
		if (capacity > MAX_CAPACITY) {
			throw new IllegalArgumentException("capacity too large: " + capacity + " max=" + MAX_CAPACITY);
		}
		//
		// SessionId是一个整数，会取罪接近的2的幂乘上一个扩大系数以避免碰撞
		this.capacity = capacity;
		this.mask = NumberUtils.NextPower2(capacity) * MAGNIFY - 1;

		head = 0;
		syncRoot = new Object();
		sessions = new HashMap<Integer, E>();
	}

	/**
	 * 
	 * 当前的Session数
	 * @return
	 */
	public int concurrent()
	{
		return sessions.size();
	}

	/**
	 * 
	 * 增加一个新的Session
	 * @param session
	 * @return
	 */
	public int add(E session)
	{
		if (sessions.size() >= capacity) {
			return -1;
		}

		synchronized (syncRoot) {
			while (true) {
				head = (head + 1) & mask;
				if (sessions.get(head) == null) {
					sessions.put(head, session);
					return head;
				}
			}
		}
	}

	/**
	 * 
	 * 按照SessionId查找一个Session
	 * @param seq
	 * @return
	 */
	public E get(int seq)
	{
		synchronized (syncRoot) {
			return sessions.get(seq);
		}
	}

	/**
	 * 
	 * 移除一个Session
	 * @param seq
	 * @return
	 */
	public E remove(int seq)
	{
		synchronized (syncRoot) {
			return sessions.remove(seq);
		}
	}

	/**
	 * 
	 * 返回所有的Session
	 * @param func
	 * @return
	 */
	public List<Entry<Integer, E>> getAllItems(Function<E, Boolean> func)
	{
		List<Entry<Integer, E>> list = new ArrayList<Entry<Integer, E>>();
		synchronized (syncRoot) {
			for (Entry<Integer, E> e : sessions.entrySet()) {
				if (func != null && !func.apply(e.getValue())) {
					continue;
				}
				list.add(e);
			}
		}
		return list;
	}
}
