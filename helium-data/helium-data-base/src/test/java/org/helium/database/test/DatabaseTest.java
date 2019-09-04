package org.helium.database.test;


import com.feinno.superpojo.util.FileUtil;
import org.helium.database.ConnectionString;
import org.helium.database.Database;
import org.helium.database.spi.DatabaseManager;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Coral on 5/5/15.
 */
public class DatabaseTest {

    @Test
    public void testSqlServer() throws Exception {
        String path = System.getProperty("user.dir") + "/urcs-data-access/build/resources/test/sqlServer_GGRPDB.properties";
        String str = new String(Files.readAllBytes(Paths.get(path)));
        ConnectionString cs = ConnectionString.fromText(str);
        Database db = DatabaseManager.INSTANCE.getDatabase("GGRPDB", cs);
        System.out.printf("sqlServer %b", db.test());
    }

    @Test
	public void testMysql() throws IOException {
		String path = "src/test/resources/db/TESTDB.properties";
		String str = FileUtil.read(path);
        ConnectionString cs = ConnectionString.fromText(str);
        Database db = DatabaseManager.INSTANCE.getDatabase("GRPDB", cs);
        System.out.printf("mysql %b", db.test());
    }

}
