package org.helium.database.test;


import org.helium.database.Database;
import org.helium.database.sharding.ShardedDatabase;
import org.helium.framework.annotations.FieldSetter;
import org.helium.framework.annotations.ServiceImplementation;

/**
 * Created by Coral on 10/12/15.
 */
@ServiceImplementation
public class SampleServiceImpl implements SampleService {

	@FieldSetter("TEST_SHARDING.xml")
	private ShardedDatabase<Long> testdb;


	@FieldSetter("0")
	private String key = "0";


	/**

	 CREATE TABLE `test_tb` (
	 `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
	 `name` varchar(20) DEFAULT NULL,
	 `desc` varchar(20) DEFAULT NULL,
	 PRIMARY KEY (`id`)
	 ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

	 */

	@Override
	public void foo() {
		try {
			for (int i = 0; i < 10; i++) {
				Database db = testdb.getSharding((long)i);
				///* 9:46:13 AM 10.10.220.93-3306 Test */ INSERT INTO `test_tb` (`id`, `name`, `desc`) VALUES (NULL, '1', '2');
				db.executeInsert("insert into test_tb ( `name`, `desc`) values(?, ?)", i, "aa" + i);
				System.out.println(">>> insert into test_tb:");
				for (Database d: testdb.getAllShardings()) {
					Long id = d.executeValue("select max(id) from test_tb", Long.class);
					System.out.println(">>> max id:" + id);
				}
			}


		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public long foo2() {
		return 0;
	}
}
