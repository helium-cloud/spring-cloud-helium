/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2011-1-24
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 * 
 * remark:mysql 需要表的引擎设为InnoDB才能支持事务 
 * alter table table_name engine=InnoDB;
 */
package org.helium.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * <b>描述：</b>事务调用类
 * <p>
 * <b>功能：</b>启动一个事务
 * <p>
 * <b>用法:</b>
 * <pre><code>
 * Transaction trans = Database.beginTransaction();
 * try{
 * 	trans.spExcuteNoQuery();
 * 	trans.spExcuteNoQuery();
 * 	trans.commit();
 * }catch(Exception ex)
 * {
 * 	trans.rollback();
 * }
 * finally
 * {
 * 	trans.close();
 * }
 * </code></pre>
 * <b>备注:</b>mysql 表的engine=InnoDB,否则事务不起作用
 * Created by Coral
 */
//public class Transaction implements Operation
public interface Transaction extends Database
{
	/**
	 * 释放数据库连接，用户在不使用Transaction对象后必须显示执行此方法
	 */
	void close();

	/**
	 * 事务提交
	 */
	void commit() throws SQLException;

	/**
	 * 事务回滚
	 */
	void rollback() throws SQLException;

	Connection getConnection();
}
