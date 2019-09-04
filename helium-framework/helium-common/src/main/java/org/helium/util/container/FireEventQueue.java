package org.helium.util.container;

import org.helium.util.Action;
import org.helium.util.ConcurrentFixedSizeQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 这是一个具有入列时会自动响应事件的队列，当队列中进入某些数据，可以激活自定义的Action进行处理<br>
 * 所有的入列操作与Action响应处理均是异步,响应的处理速度不会影响到主线程的使用，不会使主线程阻塞<br>
 * 
 * @author Lv.Mingwei
 * 
 * @param <E>
 */
public abstract class FireEventQueue<E> {

	/**
	 * 一个可以快速响应添加事件的队列，一旦有数据入队，就会触发Action<br>
	 * 为防止消费速度低于生产速度而产生内存溢出，此队列可限制容量 <code>capacity</code>
	 * ，但请注意，当容量为0时，代表无缓存限制，会默认使用{@link Integer#MAX_VALUE} 作为其上限
	 * 
	 * @param capacity
	 * @param fireEvent
	 * @return
	 */
	public static <E> FireEventQueue<E> newQuickFireEventQueue(int capacity, Action<E> fireEvent) {
		return new QuickFireEventQueue<E>(capacity, fireEvent);
	}

	/**
	 * 同步处理事件的队列，该队列在相同线程内为加入进来的元素调用处理方法
	 * 
	 * @param fireEvent
	 * @return
	 */
	public static <E> FireEventQueue<E> newSyncFireEventQueue(Action<E> fireEvent) {
		return new SyncFireEventQueue<E>(fireEvent);
	}

	/**
	 * 一个具有缓存效果的支持并发队列，当缓存到一定值<code>cacheSize</code>或达到指定间隔时间
	 * <code>intervalTime</code>时，激活Action<br>
	 * 但请注意，当缓存值为0时，代表无缓存大小限制，会默认使用 {@link Integer#MAX_VALUE}
	 * 作为其上限,当间隔时间为0时，会默认不采用间隔时间作为限制，也就是说当两个值同时为0时，此队列无意义，构造方法会抛出异常，
	 * 以禁止创建这样的无意义对象，当缓存值为1时，推荐使用<code>QuickFireEventQueue</code>
	 * 
	 * @param cacheSize
	 * @param intervalTime
	 *            请注意，此时间单位为毫秒
	 * @param fireEvent
	 * @return
	 */
	public static <E> FireEventQueue<E> newCacheFireEventQueue(int cacheSize, long intervalTime,
			Action<Queue<E>> fireEvent) {
		return new CacheFireEventQueue<E>(cacheSize, intervalTime, fireEvent);
	}

	/**
	 * 它是一个像保鲜箱一样的队列，保鲜箱里面只存储新鲜的东西，过期的东西会被处理掉，而处理方法就是调用
	 * {@link Action#run(Object)}
	 * 方法，箱子中不会存储完全相同的食物，当同一个食物被多次添加时，也就是调用add方法的时候，那么会认为是更新这个食物的入箱时间，
	 * 判断是否为同一个食物的标准为调用该食物的equals方法
	 * <p>
	 * 应用场景：<br>
	 * 1. 防止方法重入:当服务端在同一时刻同一个数据有多次变更时，会多次通知客户端去服务器端取数据，而其实客户端仅需在最后一刻取一次数据既可，
	 * 无需在同一秒钟多次的向服务端发送取数据的请求
	 * ，所以如果把要取的服务器信息名称放入此箱子中，每次相同的信息名称存入会被认为是一次更新入箱时间，因此箱子会等待同一信息稳定一定时间后
	 * ，再去调用回调Action执行取数据的方法
	 * <p>
	 * 2.
	 * 对客户端活跃状态的监控：服务端以心跳包来判断某一个客户端是否存活，如果将每次的心跳信息放入保鲜箱中，当间隔一段时间某一个客户端不再发送心跳包了
	 * ,保鲜箱没有再更新此客户端的最新更新时间，保鲜箱会认为该食物已过期，会调用Action的run方法进行处理
	 * 
	 * @param freshTime
	 *            保鲜时间，入箱超过此时间的将被移出，并调用Action进行处理,请注意，此时间单位为毫秒
	 * @param fireEvent
	 *            超过保质期后的食物会被调用此Action的run方法进行处理
	 * @return
	 */
	public static <E> FireEventQueue<E> newFreshBoxFireEventQueue(long freshTime, Action<E> fireEvent) {
		return new FreshBoxFireEventQueue<E>(freshTime, fireEvent);
	}

	/**
	 * 加入队列的方法
	 * 
	 * @param e
	 */
	public abstract void add(E e);

	/**
	 * 强制关闭队列,对剩余数据做丢弃处理
	 */
	public abstract void close();
}

/**
 * 具有并发操作以及二级缓存的一个队列，在缓存满后或间隔指定时间后会自动触发事件<br>
 * 这里像是一个快递公司，当攒够指定数量的货物时会统一发货给货物的接受者，当然也可以在创建公司的时候选择定时发货的模式，间隔指定时间发一批货
 * 
 * @author Lv.Mingwei
 * 
 * @param <E>
 */
class CacheFireEventQueue<E> extends FireEventQueue<E> {

	// 一级写缓存
	private Queue<E> writeCacheQueue1;

	// 二级写缓存，当一级写满后，由此二级缓存暂时代理一级缓存的写功能，之后一级缓存会与读队列进行切换，切换后，此二级缓存会升级为一级缓存
	private Queue<E> writeCacheQueue2;

	// 读缓存,一级缓存满后，且消费者已将上一个读缓存消费完了，此时会切换一级缓存为读缓存
	private Queue<E> readQueue;

	// 切换操作是否准备好，当消费者将当前的读缓存消费完毕后，此标识会被置为true,表示可以再次进行读写切换
	private volatile boolean isReadySwitch = true;

	// 扫描间隔时间，由用户在构造方法中定义，如果定义为0，那么默认不进行扫描，此间隔时间表示上一次消费时间与当前时间的间距
	private long intervalTime = 0;

	// 满足条件后触发的事件，由用户自定义
	private Action<Queue<E>> fireEvent;

	// 雇佣的王小二，负责仓库满了的时候或者间隔指定时间去送货
	private MonitorThread monitorThread = new MonitorThread();

	// 是否运行
	private boolean isRun = true;

	// 缓存大小，超过此大小后会进行读写切换，切换后调用Action处理缓存中的元素
	private int cacheSize = Integer.MAX_VALUE;

	private static final Logger LOGGER = LoggerFactory.getLogger(CacheFireEventQueue.class);

	/**
	 * 构造方法
	 * 
	 * @param cacheSize
	 *            缓存大小,超过此大小后,将被触发fireEvent事件
	 * @param intervalTime
	 *            间隔时间，超过间隔时间，将被触发fireEvent事件
	 * @param fireEvent
	 *            当满足前两个参数条件后，此对象的run方法会被触发
	 */
	public CacheFireEventQueue(int cacheSize, long intervalTime, Action<Queue<E>> fireEvent) {
		if (cacheSize == 0 && intervalTime == 0) {
			throw new RuntimeException("You can not create cacheSize=0 and intervalTime=0 CacheFireEventQueue Object");
		}
		this.cacheSize = cacheSize != 0 ? cacheSize : Integer.MAX_VALUE;
		this.intervalTime = intervalTime;
		this.fireEvent = fireEvent;
		this.initialize();
	}

	/**
	 * 初始化一、二级写缓存，初始化读缓存，创建监控线程
	 */
	private void initialize() {
		writeCacheQueue1 = new ConcurrentFixedSizeQueue<E>(cacheSize);
		writeCacheQueue2 = new ConcurrentFixedSizeQueue<E>(cacheSize);
		readQueue = new ConcurrentFixedSizeQueue<E>(cacheSize);
		monitorThread.setDaemon(true);// 如果都死了，也陪葬
		monitorThread.setName("FireEventMonitorThread");
		monitorThread.start();
	}

	@Override
	public void add(E e) {
		// 为了发挥并发队列的优势，入队时不进行同步，当队列已满，面临读写切换时再进行同步
		if (!writeCacheQueue1.add(e)) {
			// 一级缓存已满，此时把消息写入二级缓存中
			writeCacheQueue2.add(e);
			// 一级缓存已经攒够货了，是时候出手了
			synchronized (monitorThread) {
				if (isReadySwitch) {// 判断上次的货送完了没有，如果没送完，就先等等
					isReadySwitch = false;// 上次的货送完了，要送这次的货了，送货前先做个标识，等把这次送完了，才能再送货
					monitorThread.notify();// 叫醒雇员王小二开始送货
				}
			}
		}
	}

	/**
	 * 生意不好做了，通知王小二下岗，关门
	 */
	public void close() {
		this.isRun = false;
		// 王小二，起来收拾收拾走人吧
		synchronized (monitorThread) {
			monitorThread.notify();
		}
	}

	/**
	 * 监控线程，监控队列，外部需要保证此线程在此对象中只能存在一份<br>
	 * 
	 * @author Lv.Mingwei
	 * 
	 */
	class MonitorThread extends Thread {

		/**
		 * 本线程讲述的是《快递公司王小二》的故事--做一个快乐的程序员(^-^)
		 */
		public void run() {
			// 只要公司没有关门，我王小二就会一直做下去
			while (isRun) {
				// 小二:"整个公司只有我一个送货的，老板说是为了减少开销,喂!老板、老板娘，你们一个一个叫我，你们一起说话我忙不过来"
				synchronized (this) {
					/**
					 * Step1. 线程首先挂起，在指定间隔时间或一级写内存满了后被唤醒，开始执行内存切换以及消费操作
					 */
					try {
						if (intervalTime > 0) {
							monitorThread.wait(intervalTime); // 小二:"老板，按合同约定，没事的时候我可以休息这些"intervalTime"时间，货来了的时候记得叫我，如果没叫我，等我睡醒再去送"
						} else {
							this.wait();// 小二:"老板，我先睡觉去了，来货的时候再叫我，咱合同可没约定我休息的时间，我可是很能睡的，你不叫我，我不起来“
						}
						// 旁白：货来了，老板在主线程把王小二叫起来:"小二，来货了，快醒醒，开始送货了"。
						isReadySwitch = false; // 小二说:"好嘞，老板，我来了，这就出去送，我出去的这段时间，如果有新货过来，你先存着，等我把这批货送完，再给我"
					} catch (InterruptedException e) {
						// 小二:"老板，我病了，你给我办了医疗保险了吗?"
						isReadySwitch = true;
						LOGGER.error("CacheFireEventQueue.MonitorThread found error:{}", e);
					}
				}

				if (writeCacheQueue1.size() == 0) {
					// 王小二看了看盒子说:"老板你骗我，一级库房中根本没有货，算了，我回去睡觉去了，有货了再叫我，88"
					isReadySwitch = true;// 有货的时候记得叫我，我准备好去送货了。
					continue;// 先回去睡觉了
				}

				/**
				 * Step2. "读"、"写"内存切换
				 */
				readQueue = writeCacheQueue1;// 小二："看来真有货要送了，我直接把一号仓库变成自己的送货仓库，这样可以省去搬货的时间"
				writeCacheQueue1 = writeCacheQueue2;// 小二:"一号仓库已经变成了自己送货的仓库，为了省时间，那我把以前的二号仓库变成一号仓库吧"
				writeCacheQueue2 = new ConcurrentFixedSizeQueue<E>(cacheSize); // 小二说:"糟糕，为了省时间，二号仓库已经被我变成一号仓库了，二号仓库没了，老板向里放东西会找不到门的，他要是知道了肯定会扣我钱的，我要建一个空的二号仓库"

				/**
				 * Step3. 消费"读"内存中的数据
				 */
				try {
					fireEvent.run(readQueue); // 小二出去送货了:"先生，您定的货到了，请签收"
				} catch (Exception e) {// 防止回调中出现异常，回调中如果出现异常，不处理
					LOGGER.error("CacheFireEventQueue.MonitorThread found error,callback founction run error:{}", e);
				}
				readQueue.clear();// 收货的人签完字后，小二把自己送货的仓库打扫了一下
				isReadySwitch = true;// 小二:"好嘞，送完货了，老板，我先休息了，有事记得叫我哈!"
			}

			// 小二:"公司歇菜了...忍泪清理公司的遗物"
			writeCacheQueue1.clear();
			writeCacheQueue2.clear();
			readQueue.clear();
		}
	}
}

/**
 * 一个具有快速响应的队列，一旦有数据入队列，则激活事件处理方法，为了防止入列速度大于处理速度，构造方法中也增加了限制队列容量的参数，参数为0时， 默认使用
 * {@link Integer#MAX_VALUE}
 * 
 * @author Lv.Mingwei
 * 
 * @param <E>
 */
class QuickFireEventQueue<E> extends FireEventQueue<E> {

	// 一级写缓存
	private LinkedBlockingQueue<E> dateQueue;

	// 此Q的容量，超过此容量，则进行忽略
	private int capacity = Integer.MAX_VALUE;

	// 当有内容的时候，所触发的事件
	private Action<E> fireEvent;

	private boolean isRun = true;

	private static final Logger LOGGER = LoggerFactory.getLogger(QuickFireEventQueue.class);

	public QuickFireEventQueue(int capacity, Action<E> fireEvent) {
		this.capacity = capacity != 0 ? capacity : Integer.MAX_VALUE;
		this.fireEvent = fireEvent;
		this.initialize();
		this.fireEventListener();
	}

	private void initialize() {
		dateQueue = new LinkedBlockingQueue<E>(capacity);
	}

	@Override
	public void add(E e) {
		dateQueue.offer(e);
	}

	public void close() {
		isRun = false;
	}

	private void fireEventListener() {
		// 启动一个线程调用消费方法
		if (fireEvent != null) {
			Thread thread = new Thread() {
				@Override
				public void run() {
					while (isRun) {
						try {
							fireEvent.run(dateQueue.take());
						} catch (Exception e) {
							LOGGER.error(
									"QuickFireEventQueue.MonitorThread found error,callback fireEvent founction run error:{}",
									e);
						}
					}
					// 清理仓库
					dateQueue.clear();

				}
			};
			thread.setDaemon(true);
			thread.start();
		}
	}

}

/**
 * 
 * 简单的在同步线程内处理方法的队列(叫队列其实不准确)
 * 
 * @author Lv.Mingwei
 * 
 * @param <E>
 */
class SyncFireEventQueue<E> extends FireEventQueue<E> {

	// 当有内容的时候，所触发的事件
	private Action<E> fireEvent;

	public SyncFireEventQueue(Action<E> fireEvent) {
		this.fireEvent = fireEvent;
	}

	@Override
	public void add(E e) {
		fireEvent.run(e);
	}

	@Override
	public void close() {
		fireEvent = null;
	}

}

/**
 * 它是一个像保鲜箱一样的队列，保鲜箱里面只存储新鲜的东西，过期的东西会被处理掉，而处理方法就是调用 {@link Action#run(Object)}
 * 方法，箱子中不会存储完全相同的食物，当同一个食物被多次添加时，也就是调用add方法的时候，那么会认为是更新这个食物的入箱时间，
 * 判断是否为同一个食物的标准为调用该食物的equals方法
 * 
 * @author Lv.Mingwei
 * 
 * @param <E>
 */
class FreshBoxFireEventQueue<E> extends FireEventQueue<E> {

	/**
	 * 保鲜箱的保质期,超过此保质期,则认为食物已经腐烂,需要清理，默认保鲜时间为1000毫秒
	 */
	private long freshTime = 1000;

	/**
	 * 食物过期后调用的清理方法
	 */
	private Action<E> fireEvent;

	/**
	 * 保存食物的集合，它是一个Set，其中存储了所有食物，不过食物有一个外包装，包装上记录着食物的放入日期
	 */
	private Set<ElementsDecoration<E>> set;

	/**
	 * 是否运行监控
	 */
	private boolean isRun = true;

	private static final Logger LOGGER = LoggerFactory.getLogger(FreshBoxFireEventQueue.class);

	/**
	 * 它是一个像保鲜箱一样的队列，保鲜箱里面只存储新鲜的东西，过期的东西会被处理掉，而处理方法就是调用
	 * {@link Action#run(Object)}
	 * 方法，箱子中不会存储完全相同的食物，当同一个食物被多次添加时，也就是调用add方法的时候，那么会认为是更新这个食物的入箱时间，
	 * 判断是否为同一个食物的标准为调用该食物的equals方法
	 * 
	 * @param freshTime
	 *            保鲜箱的保质期,超过此保质期,则认为食物已经腐烂,需要清理，默认保鲜时间为1000毫秒
	 * @param fireEvent
	 */
	public FreshBoxFireEventQueue(long freshTime, Action<E> fireEvent) {
		// 保鲜箱最少的保质期是1000毫秒
		if (freshTime > 1000) {
			this.freshTime = freshTime;
		} else {
			this.freshTime = 1000;
		}
		this.fireEvent = fireEvent;
		// 存放食物的集合是一个线程安全的集合，它只有一个入口
		set = Collections.synchronizedSet(new HashSet<ElementsDecoration<E>>());
		// 创建及启动保鲜箱的扫描线程
		fireEventListener();
	}

	/**
	 * 增加一个食物到保鲜箱中，如果箱子中已经存在该食物，则把以前的食物拿出来扔掉，放进新鲜的食物进去
	 */
	@Override
	public void add(E e) {
		// 给食物装上包装，包装上写明入箱时间
		ElementsDecoration<E> elementsDecoration = new ElementsDecoration<E>(e);
		// 从一个口子打开箱子，首先查看箱子中是否有这个食物了，如果有，则把之前的扔掉，放入新的进来，如果没有，则新增加进去(如果不删除之前的元素，可恶的HashSet.add(E
		// e)方法不会对已存在的元素进行覆盖替换)
		synchronized (set) {
			if (set.contains(elementsDecoration)) {
				set.remove(elementsDecoration);
			}
			set.add(elementsDecoration);
		}
	}

	/**
	 * 停掉监控线程，也就停掉了整个保鲜箱
	 */
	@Override
	public void close() {
		isRun = false;
	}

	/**
	 * 保鲜箱的管理员大叔，他来时刻的查看保鲜箱中食物的新鲜程度，如果发现了过期的食物，则交给对象创建时指定的Action处理者<br>
	 * 查看的频率是新鲜度的二分之一个时间
	 */
	private void fireEventListener() {
		// 启动一个线程调用消费方法
		if (fireEvent != null) {
			Thread thread = new Thread() {
				@Override
				public void run() {
					long sleepTime = freshTime != 0 ? freshTime / 2 : 500;// 正常频率为间隔时间的一半，默认为500毫秒
					while (isRun) {
						synchronized (set) {
							// 箱子上锁，放到小黑屋，管理员开始检查箱子里面的食物了
							Iterator<ElementsDecoration<E>> iterable = set.iterator();
							while (iterable.hasNext()) {
								// 拿到食物后进行逐个查询，如果发现过期的了，则交给对象创建时指定的Action处理者
								ElementsDecoration<E> elementsDecoration = iterable.next();
								if (elementsDecoration.isTime()) {
									iterable.remove();
									try {
										fireEvent.run(elementsDecoration.getData());
									} catch (Exception e) {// 防止回调中出现异常导致线程终止
										LOGGER.error(
												"FreshBoxFireEventQueue.MonitorThread found error,callback fireEvent founction run error:{}",
												e);
									}
								}
							}
						}
						try {
							// 大叔也要休息
							TimeUnit.MILLISECONDS.sleep(sleepTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					// 哥们儿，下岗了，咱清理仓库吧
					set.clear();
				}
			};
			// 好吧，启动吧
			thread.setName("FreshBoxFireEventQueueMonitor");
			thread.setDaemon(true);
			thread.start();
		}
	}

	/**
	 * 一个用于包装食物的内部类，他主要是给食物加上标签，也就是入列时间
	 * 
	 * @author Lv.Mingwei
	 * 
	 * @param <E>
	 */
	@SuppressWarnings({ "rawtypes", "hiding" })
	class ElementsDecoration<E> {
		private E e;
		private long innerTime;

		public ElementsDecoration(E e) {
			this.e = e;
			this.innerTime = System.currentTimeMillis();
		}

		public final E getData() {
			return e;
		}

		/**
		 * 食物是否过期
		 * 
		 * @return
		 */
		public boolean isTime() {
			if (System.currentTimeMillis() - innerTime >= freshTime) {
				return true;
			}
			return false;
		}

		/**
		 * 判断两个食物是否相同，还是以包装类中的真正元素作为比较依据
		 */
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ElementsDecoration)) {
				return false;
			}

			ElementsDecoration elementsDecoration = (ElementsDecoration) obj;
			// 以包装类中的被包装元素为唯一比较依据
			return e != null ? e.equals(elementsDecoration.getData()) : false;
		}

		@Override
		public int hashCode() {
			return e != null ? e.hashCode() : 0;
		}

		@Override
		public String toString() {
			return "innerTime:[" + innerTime + "], data:" + e.toString();
		}
	}

}
