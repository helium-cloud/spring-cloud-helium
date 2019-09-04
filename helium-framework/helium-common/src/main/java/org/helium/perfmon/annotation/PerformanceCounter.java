package org.helium.perfmon.annotation;

import org.helium.perfmon.PerformanceCounterType;

import java.lang.annotation.*;

/**
 * 
 * <b>描述:
 * </b>该批注作用于Java类的字段上，用于标识当前字段是一个性能计数器(PerformanceCounterCategory)中所监控的一项指标,
 * 性能计数器用于帮助使用者实时监控Java方法的运行情况，该注解与{@link PerformanceCounterCategory}联合使用，
 * {@link PerformanceCounterCategory}作用在类上，而{@link PerformanceCounter}
 * 作用在字段上，用于标识该字段所计数的具体类型
 * <p>
 * <b>功能: </b>用于标识当前字段是一个性能计数器(PerformanceCounterCategory)中所监控的一项指标
 * <p>
 * <b>用法: </b>使用方式如下，下面定义了一个名字为"rpc-server"的性能计数器，该性能计数器监控一个名为tx的
 * {@link PerformanceCounterType#TRANSACTION}类型的指标
 * 
 * <pre>
 * &#064;PerformanceCounterCategory(&quot;rpc-server&quot;)
 * public static class ServerCounter {
 * 	&#064;PerformanceCounter(name = &quot;tx&quot;, type = PerformanceCounterType.TRANSACTION)
 * 	private SmartCounter tx;
 * 
 * 	public SmartCounter getTx() {
 * 		return tx;
 * 	}
 * 
 * 	public void setTx(SmartCounter tx) {
 * 		this.tx = tx;
 * 	}
 * }
 * </pre>
 * <p>
 * 
 * Created by Coral
 * @see PerformanceCounterCategory
 * @see PerformanceCounterType
 */
@Inherited
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PerformanceCounter {
	String name();

	PerformanceCounterType type();
}
