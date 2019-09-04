package org.helium.perfmon.observation;



/**
 * 计数器快照，保存某个时间点的计数器信息，
 * 可以通过不同时间的计数器快照计算出此段时间中的信息
 * 
 * Created by Coral
 */
public abstract class ObserverReportSnapshot
{
	private long nanos;
	
	public ObserverReportSnapshot()
	{
		nanos = System.nanoTime();
	}

	public long getNanos()
	{
		return nanos;
	}

	/**
	 * 
	 * 计算出报表结果
	 * @param last
	 * @return
	 */
	public abstract ObserverReportUnit computeReport(ObserverReportSnapshot last);
}
