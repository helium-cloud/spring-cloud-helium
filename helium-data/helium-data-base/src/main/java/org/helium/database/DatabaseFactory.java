package org.helium.database;

import org.helium.framework.annotations.ServiceInterface;

/**
 * 实现数据库访问的接口
 */
@ServiceInterface(id = "helium:DatabaseFactory")
public interface DatabaseFactory {
	/**
	 *
	 * @param name
	 * @return
	 */
	Database getDatabase(String name);

	/**
	 *
	 * @param name
	 * @param string
	 * @return
	 */
	Database getDatabase(String name, ConnectionString string);
}
