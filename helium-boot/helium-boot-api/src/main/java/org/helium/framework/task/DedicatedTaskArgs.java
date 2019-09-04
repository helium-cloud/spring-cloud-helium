package org.helium.framework.task;

/**
 * 针对某特定Tag的请求
 * Created by Coral on 6/27/15.
 */
public interface DedicatedTaskArgs {
	/**
	 * 获取请求特定的Tag
	 * @return
	 */
	String getTag();
}
