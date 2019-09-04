package org.helium.sample.bootstrap.quickstart;

import org.helium.database.DataRow;
import org.helium.database.DataTable;
import org.helium.database.Database;
import org.helium.framework.annotations.FieldSetter;
import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.tag.Initializer;

/**
 * quickstart示例，实现Service
 * Created by Coral on 6/15/17.
 */
@ServiceImplementation
public class SampleServiceImpl implements SampleService {
	@FieldSetter("SAMPLEDB")
	private Database db;
	
	@Initializer
	public void initialize() {
		// DO INITIALIZE
	}
	
	@Override
	public SampleUser getUser(int userId) throws Exception {
		DataTable table = db.executeTable("select * from Users where id = ?", userId);
		
		if (table.getRowCount() == 0) {
			return null;
		}

		DataRow row = table.getRow(0);
		SampleUser user = new SampleUser();
		user.setId(row.getInt("id"));
		user.setName(row.getString("name"));
		user.setRole(row.getString("role"));
		return user;
	}

	@Override
	public void insertUser(SampleUser user) throws Exception {
		db.executeInsert("insert into Users values (?, ?, ?)", 
				user.getId(), user.getName(), user.getRole());
	}
}


/*
create database SAMPLEDB;
create table Users (
	id int primary key,
	name varchar(100),
	role varchar(100)
);

insert into Users value (1, "Zhangsan", "admin");
insert into Users value (2, "Lisi", "user");
 */
