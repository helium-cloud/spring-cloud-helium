package org.helium.framework.task;

import org.helium.framework.annotations.ServiceInterface;

/**
 * 定向task，特定业务路由
 */
@ServiceInterface(id = DedicatedTagManager.ID)
public interface DedicatedTagManager {
	String ID = "helium:DedicatedTagManager";
	/**
	 * 获取Tag
	 * @param tag
	 * @return
	 */
	String getTag(String tag);

	/**
	 * 强制的put一个新值
	 * Tag
	 * @param tag
	 * @param value
	 */
	void putTag(String tag, String value);

	/**
	 * 如果已经存在值了, 就把旧的值取回来, 不在put
	 * @param tag
	 * @param value
	 * @return
	 */
	String getOrPutTag(String tag, String value);

	/**
	 * 删除Tag
	 * @param tag
	 */
	void deleteTag(String tag);
}
