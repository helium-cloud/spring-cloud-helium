package org.helium.threading;

import com.feinno.superpojo.type.EnumInteger;
import org.helium.util.Event;
import org.helium.util.EventHandler;
import org.helium.util.Result;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 
 * <b>描述: </b>这个类是控制一组{@link Future} 的帮助类，每一个{@link Future}
 * 可以代表一个线程，它可以同时等待多个线程处理完毕后，在将每个线程的处理结果收集， 又可以等待某一个{@link Future}得到结果后就进行分析，
 * 最后进行综合全部{@link Future}的结果后得到最终值， 可以想象成是一种分布式计算
 * <p>
 * <b>功能: </b>实现当前主线程与一组{@link Future}的同步或异步处理，主线程可以将计算任务分布给多个线程，每一个线程由
 * {@link Future}来传递结果,{@link FutureGroup}可以选择在全部的{@link Future}
 * 都运行结束后再统一处理结果，还是某一个{@link Future}运行结束后就处理结果
 * <p>
 * <b>用法: </b>
 * 
 * <pre>
 * 以分布式计算为例，通过将任务划分成多份，交由不同的线程并行处理，并将多个结果进行组合，最终得出综合结果，使用此方法经本机测试，耗时仅为常规计算的30-40%
 * 
 * public class Test {
 * 
 * 	public static void main(String[] args) throws Exception {
 * 		int cupNumber = Runtime.getRuntime().availableProcessors(); // 获得cpu数量
 * 		int length = Integer.MAX_VALUE / 2; // 一半的Integer最大值的等差数列进行求和(当然可以使用公式进行快速计算，但此处是为了演示效率，因此没有使用公式)
 * 		int average = (length + cupNumber - 1) / cupNumber; // 每一份任务的平均需要处理的数量
 * 		long sum = 0; // 计算结果
 * 		long startTime = System.nanoTime(); // 耗时检测
 * 		List<Future<Long>> futures = new ArrayList<Future<Long>>(); // 每一份任务都会有一个Future
 * 		for (int i = 0; i < cupNumber; i++) { // 根据Cpu核心数量，创建对应的计算任务
 * 			int start = i * average;
 * 			Future<Long> futureTemp = new Future<Long>();
 * 			futures.add(futureTemp);
 * 			// 开启计算任务
 * 			TaskThread taskThread = new TaskThread(futureTemp, start, start + average > length ? length : start
 * 					+ average);
 * 			taskThread.start();
 * 		}
 * 		FutureGroup<Future<Long>> futureGroup = new FutureGroup<Future<Long>>(futures); // 将多个任务同时放入FutureGroup中
 * 		Future<Long> future = null;
 * 		while ((future = futureGroup.awaitAny()) != null) { // 遍历每一个完成的任务，得到任务结果后计算和值
 * 			sum += future.getValue();
 * 		}
 * 		long endTime = System.nanoTime(); // 结束时间
 * 		long distributedTime = endTime - startTime;// 分布式计算耗时
 * 		System.out.println("计算结果:" + sum + "  分布式计算耗时:"
 * 				+ TimeUnit.MILLISECONDS.convert(distributedTime, TimeUnit.NANOSECONDS) + "ms.");
 * 
 * 		// 以下为传统的for循环方式计算结果
 * 		startTime = System.nanoTime(); // 传统计算的耗时检测开始
 * 		sum = 0;
 * 		for (int i = 0; i < length; i++) { // 计算同样数量的等差数列的值
 * 			sum += i;
 * 		}
 * 		endTime = System.nanoTime(); // 传统计算结束
 * 		long commonTime = endTime - startTime; // 传统计算耗时
 * 		DecimalFormat decimalFormat = new DecimalFormat("00%");
 * 		System.out.println("计算结果:" + sum + "  普通计算耗时:"
 * 				+ TimeUnit.MILLISECONDS.convert(commonTime, TimeUnit.NANOSECONDS) + "ms.");
 * 		System.out.println("分布式计算的耗时是传统计算的 "
 * 				+ (decimalFormat.format(Double.valueOf(distributedTime) / Double.valueOf(commonTime))));
 * 
 * 	}
 * 
 * }
 * 
 * class TaskThread extends Thread {
 * 	private Future<Long> future;
 * 	private int start;
 * 	private int end;
 * 
 * 	public TaskThread(Future<Long> future, int start, int end) {
 * 		this.future = future;
 * 		this.start = start;
 * 		this.end = end;
 * 	}
 * 
 * 	public void run() {
 * 		long sum = 0;
 * 		for (int i = start; i < end; i++) {
 * 			sum += i;
 * 		}
 * 		future.complete(sum);
 * 	}
 * }
 * </pre>
 * 
 * 以上示例是在分布式计算中体现了{@link FutureGroup}的作用，这些任务可以是多个本机计算任务，又可以是多个远程的服务，
 * {@link FutureGroup}
 * 的作用是将多个任务组合在一起，统一的协调管理每一个任务的处理结果，且可以选择是同步的等待每一个处理结果还是异步等待处理结果
 * ，以上示例是同步等待线程的处理结果，如果想使用异步方式处理每个任务的结果，方法为
 * {@link FutureGroup#addListener(EventHandler)} ，与
 * {@link Future#addListener(EventHandler)}类似.
 * <p>
 * 
 * @author Lv.Mingwei
 * @see Future
 * @see EventHandler
 */
@SuppressWarnings("rawtypes")
public class FutureGroup<E extends Future> {

	/** Future是否结束 */
	private boolean finished;

	/** 用户可根据需要创建事件完成后监听 */
	private Event<List<E>> listener;

	/** 此FutureGroup中保存的全部futures */
	private Collection<E> futures;

	/** 用于存储已经处理完毕的future */
	private List<E> completionFutures;

	/**
	 * 此Map用于记录当前线程所访问到第几个Future，这个是给awaitAny方法准备的因为awaitAny每次等待并返回一个，
	 * 因此要记录当前的位置
	 */
	private final Map<Thread, Integer> futureIndexThreadMap;

	private final Sync sync;

	@SuppressWarnings("unchecked")
	public FutureGroup(Collection<E> futures) {
		this.futures = futures;
		finished = false;
		sync = new Sync(futures.size());
		listener = new Event<List<E>>(this);
		completionFutures = Collections.synchronizedList(new ArrayList<E>());
		futureIndexThreadMap = Collections.synchronizedMap(new HashMap<Thread, Integer>());
		for (final E future : futures) {
			future.addListener(new FutureListener<Object>() {
				@Override
				public void run(Result<Object> result) {
					completionFutures.add(future);
					sync.releaseShared(0);
				}
			});
		}
	}

	/**
	 * 重载构造方法，使其可以使用可变参数
	 * 
	 * @param futures
	 */
	@SafeVarargs
	public FutureGroup(E... futures) {
		this(Arrays.asList(futures));
	}

	/**
	 * 获得在构建对象时传入的全部Future，此方法不会阻塞线程
	 * 
	 * @return
	 */
	public Collection<E> getFutures() {
		return futures;
	}

	/**
	 * 获得当前FutureGroup是否结束，此方法不会阻塞线程
	 * 
	 * @return
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * 
	 * 添加完成的listener，如果此FutureGroup已经完成了运行，则直接执行此listener
	 * 
	 * @param listener
	 */
	public void addListener(EventHandler<List<E>> handler) {
		synchronized (listener) {
			// 如果使用当前状态尝试获取资源成功了，那么本次已经满足结束条件了，因此可以执行run了
			if (sync.tryAcquireShared(WaitMode.WAIT_ALL.intValue()) > 0) {
				handler.run(this, completionFutures);
				listener.addListener(handler);
			} else {
				listener.addListener(handler);
			}
		}
	}

	/**
	 * 等待任何一个完成，等待时间将阻塞当前线程
	 * 
	 * @throws InterruptedException
	 */
	public E awaitAny() throws InterruptedException {
		sync.acquireSharedInterruptibly(WaitMode.WAIT_ANY.intValue());
		int index = futureIndexThreadMap.get(Thread.currentThread()) - 1;
		return index < completionFutures.size() ? completionFutures.get(index) : null;
	}

	/**
	 * 在指定时间内等待任何一个完成，等待时间将阻塞当前线程
	 * 
	 * @param milliseconds
	 * @throws InterruptedException
	 */
	public E awaitAny(long milliseconds) throws InterruptedException {
		if (sync.tryAcquireSharedNanos(WaitMode.WAIT_ANY.intValue(), TimeUnit.MILLISECONDS.toNanos(milliseconds))) {
			int index = futureIndexThreadMap.get(Thread.currentThread()) - 1;
			return index < completionFutures.size() ? completionFutures.get(index) : null;
		} else {
			throw new RuntimeException("Time out." + milliseconds);
		}
	}

	/**
	 * 等待全部完成，等待时间将阻塞当前线程
	 * 
	 * @throws InterruptedException
	 */
	public Collection<E> awaitAll() throws InterruptedException {
		sync.acquireSharedInterruptibly(WaitMode.WAIT_ALL.intValue());
		return futures;
	}

	/**
	 * 在指定时间内等待全部完成，等待时间将阻塞当前线程
	 * 
	 * @param milliseconds
	 * @throws InterruptedException
	 */
	public Collection<E> awaitAll(long milliseconds) throws InterruptedException {
		if (sync.tryAcquireSharedNanos(WaitMode.WAIT_ALL.intValue(), TimeUnit.MILLISECONDS.toNanos(milliseconds))) {
			return futures;
		} else {
			throw new RuntimeException("Time out." + milliseconds);
		}
	}

	/**
	 * 当前执行完毕后，会调用此方法，但是完毕不代表全部完毕，而有可能使用了WaitMode.WAIT_ANY模式，某一个Future结束就当做结束
	 */
	void finish() {
		finished = true;
		listener.fireEvent(completionFutures);
	}

	/**
	 * 同步控制器，目的为实现阻塞，继承自AQS
	 * 
	 * @author Lv.Mingwei
	 * 
	 */
	private final class Sync extends AbstractQueuedSynchronizer {

		private static final long serialVersionUID = 508099502766997287L;

		/** 此count记录当前一共有几个Future */
		private int count;

		/** 这个构造方法传入的count为所监听的future的数量 */
		Sync(int count) {
			this.count = count;
			setState(count);
		}

		/**
		 * 在获取许可的时候，如果当前的Listener状态为WAIT_ALL，那么需要计数都达到0时才可以<br>
		 * 如果当前的Listener状态为WAIT_ANY，那么随便一个释放了资源就可以<br>
		 */
		@Override
		public int tryAcquireShared(int acquires) {
			if (acquires == WaitMode.WAIT_ALL.intValue()) {
				return getState() == 0 ? 1 : -1;
			} else {
				Thread currentThread = Thread.currentThread();
				if (!futureIndexThreadMap.containsKey(currentThread)) {
					futureIndexThreadMap.put(currentThread, 0);
				}
				// (count - getState())
				// 为已完成的数量，下面语句意为如果当前线程读取Future的个数小于这个偏移量或者已经执行完毕了,那么当前加1并返回成功
				if (futureIndexThreadMap.get(currentThread) < (count - getState()) || getState() == 0) {
					futureIndexThreadMap.put(currentThread, futureIndexThreadMap.get(currentThread) + 1);
					return 1;
				} else {
					return -1;
				}
			}
		}

		/**
		 * 在每次释放资源的时候，都递减，如果递减到了0，那么通知finish方法
		 */
		@Override
		public boolean tryReleaseShared(int releases) {
			for (;;) {
				int state = getState();
				if (state == 0)
					return false;
				int nextState = state - 1;
				if (compareAndSetState(state, nextState)) {
					if (nextState == 0) {// 如果递减到了0，那么此时返回成功，主动通知等待的线程调用tryAcquireShared方法来获取许可
						finish();
						return true;
					} else if (releases != WaitMode.WAIT_ALL.intValue()) {// 如果当前状态不是waitAll,那么任何一个Future释放资源了，都要通知其他线程
						return true;
					} else {// 否则不通知任何线程，让他们继续等吧
						return false;
					}
				}
			}
		}

	}
}

/**
 * 
 * <b>描述: </b>等待的状态
 * 
 * @author Lv.Mingwei
 * 
 */
enum WaitMode implements EnumInteger {

	WAIT_ALL(1), WAIT_ANY(2);

	int value = 0;

	private WaitMode(int value) {
		this.value = value;
	}

	public int intValue() {
		return value;
	}
}
