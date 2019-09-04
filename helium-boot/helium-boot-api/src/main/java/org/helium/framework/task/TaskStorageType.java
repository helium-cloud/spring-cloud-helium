package org.helium.framework.task;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务存储类型
 *
 * task先存储再进行消费，
 * 解决服务重启、队列溢出等情况导致的数据丢失的问题
 * @author wudashuai
 * @date   2018-10-31
 */
public enum  TaskStorageType {

	MEMORY("MEMORY"),
	REDIS("REDIS"),
	MYSQL("MYSQL"),
	HBASE("HBASE"),;

	private String value;

	TaskStorageType(String value) {
		this.value = value;
	}

	public String strValue() {
		return value;
	}

	private static Map<String, TaskStorageType> types;

	public static TaskStorageType fromText(String value) {
		if (types == null) {
			types = new HashMap<>();
			for (TaskStorageType e: TaskStorageType.values()) {
				types.put(e.strValue().toUpperCase(), e);
			}
		}
		TaskStorageType r = types.get(value.toUpperCase());
		if (r == null) {
			throw new IllegalArgumentException("Unknown TaskStorageType:" + value);
		}
		return r;
	}

	public static final String MEMORY_TYPE = "MEMORY";
	public static final String REDIS_TYPE = "REDIS";
	public static final String MYSQL_TYPE = "MYSQL";
	public static final String HBASE_TYPE = "HBASE";

}
