package test.org.helium.threading;

import org.helium.threading.Future;
import org.helium.threading.FutureGroup;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestFutureGroup {
	public static void main(String[] args) throws Exception {
		int cupNumber = Runtime.getRuntime().availableProcessors(); // 获得cpu数量
		long length = Integer.MAX_VALUE; // 对Integer最大值进行等差数列求和(当然可以使用公
											// 式进行快速计算，但此处是为了演示效率)
		long average = (length + cupNumber - 1) / cupNumber; // 每一份任务的平均需要处理的数量
		long sum = 0; // 计算结果
		long startTime = System.nanoTime(); // 耗时检测
		List<Future<Long>> futures = new ArrayList<Future<Long>>(); // 每一份任务都会有一个Future
		for (int i = 0; i < cupNumber; i++) { // 根据cpu核心数量，创建对应的计算任务
			long start = i * average;
			Future<Long> futureTemp = new Future<Long>();
			futures.add(futureTemp);
			// 开启计算任务
			TaskThread taskThread = new TaskThread(futureTemp, start, start + average > length ? length : start
					+ average);
			taskThread.start();
		}
		FutureGroup<Future<Long>> futureGroup = new FutureGroup<Future<Long>>(futures); // 将多个任务同时放入FutureGroup中
		Future<Long> future = null;
		while ((future = futureGroup.awaitAny()) != null) { // 遍历每一个完成的任务，得到任务结果后计算和值
			sum += future.getValue();
		}
		long endTime = System.nanoTime(); // 结束时间
		long distributedTime = endTime - startTime;// 分布式计算耗时
		System.out.println("计算结果:" + sum + "  分布式计算耗时:"
				+ TimeUnit.MILLISECONDS.convert(distributedTime, TimeUnit.NANOSECONDS) + "ms.");

		// 以下为传统的for循环方式计算结果
		startTime = System.nanoTime(); // 传统计算的耗时检测开始
		sum = 0;
		for (int i = 0; i < length; i++) { // 计算同样数量的等差数列的值
			sum += i;
		}
		endTime = System.nanoTime(); // 传统计算结束
		long commontime = endTime - startTime; // 传统计算耗时
		DecimalFormat decimalformat = new DecimalFormat("00%");
		System.out.println("计算结果:" + sum + "  普通计算耗时:"
				+ TimeUnit.MILLISECONDS.convert(commontime, TimeUnit.NANOSECONDS) + "ms.");
		System.out.println("分布式计算的耗时是传统计算的 "
				+ (decimalformat.format(Double.valueOf(distributedTime) / Double.valueOf(commontime))));
		System.exit(0);
	}
}

class TaskThread extends Thread {
	private Future<Long> future;
	private long start;
	private long end;

	public TaskThread(Future<Long> future, long start, long end) {
		this.future = future;
		this.start = start;
		this.end = end;
	}

	public void run() {
		long sum = 0;
		for (long i = start; i < end; i++) {
			sum += i;
		}
		future.complete(sum);
	}
}