package org.helium.framework.module;

/**
 * 终止处理
 * Created by Coral on 7/7/15.
 */
public interface ModuleContext {
	/**
	 * 是否终结
	 * @return
	 */
	boolean isTerminated();

	/**
	 * 设置是否已经终结
	 * @param value
	 */
	void setIsTerminated(boolean value);

	/**
	 *
	 * @param key
	 * @param value
	 */
	void putModuleData(Object key, Object value);

	/**
	 *
	 * @param key
	 * @return
	 */
	Object getModuleData(Object key);
}
