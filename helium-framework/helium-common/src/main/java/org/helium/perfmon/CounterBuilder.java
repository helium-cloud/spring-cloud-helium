package org.helium.perfmon;

import org.helium.perfmon.observation.ObserverReportColumn;

/**
 * 抽象构造器
 * 
 * Created by Coral
 */
public abstract class CounterBuilder
{
	public abstract AbstractCounterEntity createCounter();
	
	public abstract ObserverReportColumn[] getColumns(String name);
}
