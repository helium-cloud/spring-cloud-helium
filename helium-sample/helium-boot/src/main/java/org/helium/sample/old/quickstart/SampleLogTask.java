package org.helium.sample.bootstrap.quickstart;

import org.helium.database.Database;
import org.helium.framework.annotations.FieldSetter;
import org.helium.framework.annotations.TaskImplementation;
import org.helium.framework.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by Coral on 6/27/17.
 */
@TaskImplementation(event = SampleLogTask.EVENT_NAME)
public class SampleLogTask implements Task<SampleLogTaskArgs> {
	public static final String EVENT_NAME = "quickstart:SampleLogTask";
	public static final Logger LOGGER = LoggerFactory.getLogger(SampleLogTask.class);
 	
	@FieldSetter("SAMPLEDB")
	private Database db;
	
	@Override
	public void processTask(SampleLogTaskArgs args) {
		try {
			String sql = "insert into UserLogs (time, clientIp, action, user) values (?, ?, ?, ?)";
			db.executeInsert(sql, new Date(), args.getClientIp(), args.getAction(), args.getUser().toJsonObject().toString());
		} catch (Exception ex) {
			LOGGER.error("processLogTaskFailed {}", ex);
		}
	}
}


/* 
参考SQL

create database SAMPLEDB;

create table UserLogs (
	id bigint primary key auto_increment,
	time timestamp(6),
	clientIp varchar(100),
	action varchar(100),
	user varchar(1000)
);



 */
