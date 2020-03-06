package org.helium.database.spi;

import org.helium.database.ConnectionString;
import org.helium.database.Database;
import org.helium.database.DatabaseFactory;
import org.helium.database.DatabaseOperator;
import org.helium.framework.BeanContext;
import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ServiceImplementation
public class DatabaseManager implements DatabaseFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManager.class);
	private static final String DB_CONFIG_PATH = "db" + File.separator;

	public static final DatabaseManager INSTANCE = new DatabaseManager();

	private DataSourceProvider dsProvider;
	private ConfigProvider configProvider;
	private Map<String, Database> databases;

	/**
	 * 初始化对象
	 */
	private DatabaseManager() {
		databases = new HashMap<String, Database>();
		dsProvider = new HikariDataSourceProvider();

		//
		// 如果BeanContextService已经初始化了, 则使用内置的ConfigProvider
		if (BeanContext.getContextService() != null) {
			configProvider = BeanContext.getContextService().getService(ConfigProvider.class);
		}
	}

	/**
	 * 获取一个Database
	 *
	 * @param dbName
	 * @return
	 */
	public Database getDatabase(String dbName) {
		Database db = databases.get(dbName);
		if (db != null) {
			return db;
		}
		if (configProvider != null) {
			String txt = configProvider.loadText(DB_CONFIG_PATH + dbName + ".properties");
			ConnectionString connStr;
			try {
				connStr = ConnectionString.fromText(txt);
			} catch (IOException e) {
				throw new IllegalArgumentException("Bad Connection String:" + txt);
			}
			if (connStr == null) {
				throw new IllegalArgumentException("Unknown Database:" + dbName);
			}
			db = getDatabase(dbName, connStr);
		}
		return db;
	}

	/**
	 * 获取一个数据库
	 *
	 * @param dbName
	 * @param connStr
	 * @return
	 */
	public Database getDatabase(String dbName, ConnectionString connStr) {
		synchronized (this) {
			Database db = databases.get(dbName);
			if (db != null) {
				return db;
			}
		}
		DataSource ds = dsProvider.getDataSource(connStr);
		DatabaseImpl db = new DatabaseImpl(dbName, ds);
		String tableSchema = parseTableSchema(connStr);
		db.setTableSchema(tableSchema);

		synchronized (this) {
			if (!databases.containsKey(dbName)) {
				databases.put(dbName, db);
			}
		}
		return db;
	}

	/**
	 * 获取一个数据库
	 *
	 * @param dbName
	 * @param connStr
	 * @return
	 */
	public Database getAndUpdateDatabase(String dbName, ConnectionString connStr) {
		//检测当前key是否存在
		synchronized (this) {
			Database db = databases.get(dbName);
			if (db != null) {
				databases.remove(dbName);
			}
		}
		//检测当前链接数据库
		DataSource ds = dsProvider.getDataSource(connStr);
		DatabaseImpl db = new DatabaseImpl(dbName, ds);
		String tableSchema = parseTableSchema(connStr);
		db.setTableSchema(tableSchema);

		//更新至缓存
		synchronized (this) {
			if (!databases.containsKey(dbName)) {
				databases.put(dbName, db);
			}
		}
		return db;
	}


	/**
	 * 获取一个具备建表功能的数据库连接
	 *
	 * @param dbName
	 * @param connStr
	 * @return
	 */
	public DatabaseOperator getDatabaseOperator(String dbName, ConnectionString connStr) {
		Database db;
		synchronized (this) {
			db = databases.get(dbName);
			if (db != null && db instanceof DatabaseOperator) {
				return (DatabaseOperator) db;
			}
		}

		if (connStr != null) {
			DataSource ds = dsProvider.getDataSource(connStr);
			DatabaseOperatorImpl operator = new DatabaseOperatorImpl(dbName, ds);

			synchronized (this) {
				if (!databases.containsKey(dbName)) {
					databases.put(dbName, operator);
				}
			}
			return operator;
		} else {
			if (db == null) {
				throw new IllegalArgumentException("Database not found:" + dbName);
			}

			DataSource ds = ((DatabaseImpl) db).getDataSource();
			DatabaseOperatorImpl operator = new DatabaseOperatorImpl(dbName, ds);
			operator.setTableSchema(((DatabaseImpl) db).getTableSchema());
			return operator;
		}
	}

	/**
	 * 获取一个具有建表功能的数据库客户端
	 *
	 * @param db
	 * @return
	 */
	public DatabaseOperator getDatabaseOperator(Database db) {
		return this.getDatabaseOperator(db.getName(), null);
	}

	/**
	 * 初始化数据库名
	 *
	 * @param connStr
	 */
	public String parseTableSchema(ConnectionString connStr) {
		String tableSchema = null;
		if (!StringUtils.isNullOrEmpty(connStr.getProperty("Database"))) {
			tableSchema = connStr.getProperty("Database");
		} else {
			String jdbcUrl = connStr.getJdbcUrl();
			if (jdbcUrl.lastIndexOf("/") > 0 && jdbcUrl.lastIndexOf("/") != jdbcUrl.length()) {
				tableSchema = jdbcUrl.substring(jdbcUrl.lastIndexOf("/") + 1, jdbcUrl.length());
				if (tableSchema.indexOf("?") > 0) {
					tableSchema = tableSchema.substring(0, tableSchema.indexOf("?"));
				}
			}
		}
		if (tableSchema == null) {
			throw new IllegalArgumentException("parse TableSchema from connStr:" + connStr.toString() + " failed");
		}
		return tableSchema;
	}
}
