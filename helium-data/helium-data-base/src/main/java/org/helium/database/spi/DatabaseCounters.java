/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Nov 9, 2011
 *
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.database.spi;

import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.perfmon.PerformanceCounterType;
import org.helium.perfmon.SmartCounter;
import org.helium.perfmon.annotation.PerformanceCounter;
import org.helium.perfmon.annotation.PerformanceCounterCategory;

/**
 * <b>描述：</b>数据库性能计数器
 * <p>
 * <b>功能：</b>对数据库访问的频率、次数、进行计数。参考{@link PerformanceCounterFactory}
 *
 * Created by Coral
 */
@PerformanceCounterCategory("database")
public class DatabaseCounters {
	@PerformanceCounter(name = "tx", type = PerformanceCounterType.TRANSACTION)
	private SmartCounter tx;

	public SmartCounter getTx() {
		return tx;
	}

	public void setTx(SmartCounter tx) {
		this.tx = tx;
	}
}

