package org.helium.database.test;

import org.helium.database.Column;
import org.helium.database.Database;
import org.helium.database.DatabaseOperator;
import org.helium.database.TableSchema;
import org.helium.database.spi.DatabaseManager;
import org.helium.framework.annotations.FieldSetter;
import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.test.ServiceForTest;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Coral on 8/15/16.
 */
@ServiceImplementation()
public class DatabaseOperatorTest implements ServiceForTest {
	@FieldSetter("TESTDB")
	private Database db;

	@Override
	public void test() throws Exception {
		DatabaseOperator operator = DatabaseManager.INSTANCE.getDatabaseOperator(db);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String tableName = "TEST_" + format.format(new Date());
		TableSchema schema = createTableSchema(tableName);
		if (!operator.isTableExists(tableName)) {
			operator.createTable(schema);
		}
	}

	/**
	 * 创建一个新的日志表结构
	 *
	 * -- drop table LOG_OPERATION
	 CREATE TABLE `LOG_Operation_20160830_00` (
	 `Id` bigint(20) NOT NULL AUTO_INCREMENT,
	 `OwnerId` bigint(20) NOT NULL,
	 `LogTime` timestamp(6) NOT NULL,
	 `Epid` varchar(256) NOT NULL,
	 `Operation` varchar(128) NOT NULL,
	 `PeerUri` varchar(128) NOT NULL,
	 `ResultCode` int NOT NULL,
	 `Args` varchar(8000) NOT NULL,
	 `Result` varchar(8000) NOT NULL,
	 `CostMs` decimal(13, 3) NOT NULL,
	 `Producer` varchar(64) NOT NULL,
	 `ServiceId` varchar(64) NOT NULL,
	 PRIMARY KEY (`Id`),
	 INDEX(`OwnerId`, `LogTime`)
	 ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

	 CREATE TABLE `TEST_20160815` (
	 `Id` bigint(63) NOT NULL AUTO_INCREMENT,
	 `OwnerId` bigint(63) NOT NULL,
	 `LogTime` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
	 `Epid` varchar(256) NOT NULL,
	 `Operation` varchar(128) NOT NULL,
	 `ResultCode` int(11) NOT NULL,
	 `Args` varchar(8000) NOT NULL,
	 `Result` varchar(8000) NOT NULL,
	 `CostMs` decimal(13,3) NOT NULL,
	 `Producer` varchar(64) NOT NULL,
	 `ServiceId` varchar(64) NOT NULL,
	 PRIMARY KEY (`Id`),
	 KEY `OwnerId` (`OwnerId`,`LogTime`)
	 ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
	 *
	 * @param tablename
	 * @return
	 */
	private TableSchema createTableSchema(String tablename) {
		TableSchema table = new TableSchema(tablename);
		table.addColumn(Column.createAutoIncrementLongColumn("Id"));
		table.addColumn(Column.createLongColumn("OwnerId", false, null));
		table.addColumn(Column.createTimestampColumn("LogTime", false, null));
		table.addColumn(Column.createVarcharColumn("Epid", 256, false, ""));
		table.addColumn(Column.createVarcharColumn("Operation", 128, false, ""));
		table.addColumn(Column.createIntColumn("ResultCode", false, null));
		table.addColumn(Column.createVarcharColumn("Args", 8000, false, ""));
		table.addColumn(Column.createVarcharColumn("Result", 8000, false, ""));
		table.addColumn(Column.createDecimalColumn("CostMs", "13,3", false, null));
		table.addColumn(Column.createVarcharColumn("Producer", 64, false, ""));
		table.addColumn(Column.createVarcharColumn("ServiceId", 64, false, ""));
		table.setExtension("INDEX (`OwnerId`, `LogTime`)");
		table.setTail("ENGINE=InnoDB DEFAULT CHARSET=utf8");

		return table;
	}
}
