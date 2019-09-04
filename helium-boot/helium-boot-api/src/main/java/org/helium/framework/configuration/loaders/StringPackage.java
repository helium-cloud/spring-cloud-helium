package org.helium.framework.configuration.loaders;

import org.helium.framework.annotations.FieldLoaderType;

import java.util.List;

/**
 * Created by Coral on 10/12/15.
 */
@FieldLoaderType(loaderType = StringPackageLoader.class)
public interface StringPackage {
	/**
	 * 是否包含此号码
	 * @param s
	 * @return
	 */
	boolean hasEntry(String s);

	/**
	 * 获取StringList
	 * @return
	 */
	List<String> getStringList();

	/**
	 * 判断是否为空文件
	 * @return
	 */
	boolean isEmpty();

	void refresh(String file);
}
