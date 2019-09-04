//package org.helium.sample.old.advanced;
//
//import org.helium.database.DataRow;
//import org.helium.database.DataTable;
//import org.helium.database.Database;
//import org.helium.database.sharding.DatabaseSharding;
//import org.helium.database.sharding.ShardedDatabase;
//import org.helium.framework.annotations.FieldSetter;
//import org.helium.framework.annotations.ServiceImplementation;
//import org.helium.sample.old.quickstart.SampleService;
//import org.helium.sample.old.quickstart.SampleUser;
//
///**
// * Created by Coral on 7/18/17.
// */
//@ServiceImplementation
//public class ShardedSampleServiceImpl implements SampleService {
//	@FieldSetter("SAMPLEDB_Sharding.xml")
//	private ShardedDatabase<Long> shardedDb;
//
//	@Override
//	public SampleUser getUser(int userId) throws Exception {
//		// 首先根据userId得到sharding, 然后再按照原有方式进行访问
//		Database sharding = shardedDb.getSharding((long)userId);
//		// SQL涉及到的表名后面必须添加${SHARDING}后缀，这个是约定
//		DataTable table = sharding.executeTable("select * from Users_${SHARDING} where id = ?", userId);
//
//		if (table.getRowCount() == 0) {
//			return null;
//		}
//
//		DataRow row = table.getRow(0);
//		SampleUser user = new SampleUser();
//		user.setId(row.getInt("id"));
//		user.setName(row.getString("name"));
//		user.setRole(row.getString("role"));
//		return user;
//	}
//
//	@Override
//	public void insertUser(SampleUser user) throws Exception {
//		Database sharding = shardedDb.getSharding((long)user.getId());
//		sharding.executeInsert("insert into Users_${SHARDING} values (?, ?, ?)",
//				user.getId(), user.getName(), user.getRole());
//	}
//}
//
//
///*
//create database SAMPLEDB_1;
//use SAMPLEDB_1;
//
//create table Users_0 (id int primary key, name varchar(100), role varchar(100));
//create table Users_1 (id int primary key, name varchar(100), role varchar(100));
//create table Users_2 (id int primary key, name varchar(100), role varchar(100));
//create table Users_3 (id int primary key, name varchar(100), role varchar(100));
//
//insert into Users_1 value (1, "Zhangsan", "admin");
//insert into Users_2 value (2, "Lisi", "user");
//
//create database SAMPLEDB_2;
//use SAMPLEDB_2;
//
//create table Users_4 (id int primary key, name varchar(100), role varchar(100));
//create table Users_5 (id int primary key, name varchar(100), role varchar(100));
//create table Users_6 (id int primary key, name varchar(100), role varchar(100));
//create table Users_7 (id int primary key, name varchar(100), role varchar(100));
//
//insert into Users_6 value (6, "6666666", "admin");
//insert into Users_7 value (7, "SEVEN", "user");
// */
