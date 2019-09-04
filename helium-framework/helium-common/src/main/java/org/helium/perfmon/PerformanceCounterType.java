package org.helium.perfmon;

/**
 * 计数器类型
 * 
 * Created by Coral
 */
public enum PerformanceCounterType {
	/**
	 * 单一简单数值, 仅支持一个固定的值(增加或减少), 用于单一技术场合
	 */
	NUMBER,

	/**
	 * 记录每秒访问量, 及总访问量
	 */
	QPS,

	/**
	 * 流量, 同时记录流量与次数, 总流量与平均每秒流量
	 */
	THROUGHPUT,

	/**
	 * 命中率, 同时记录命中次数与未命中次数, 计算命中率
	 */
	RATIO,

	/**
	 * 事务计数器, 记录总事务数, 并发, 平均延时, 成功数, 失败数, 最后一次错误
	 */
	TRANSACTION,
}
