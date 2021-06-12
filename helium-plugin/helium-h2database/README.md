## h2database

### 1.简介

H2 是一个使用 Java 编写的数据库，支持内存、文件等多种模式，经常用于项目的测试环境。除此之外，通过 H2 的官网了解到，H2 还提供了丰富的特性。
官网连接(https://h2database.com/html/main.html)

### 2.特性

* 性能：
    * 与 SQLit 相比较，读操作更快，但是在连接、写操作性能都不如 SQLite。
* 功能：
    * 支持全文检索，提供了内置全文检索和使用 Apache Luncene 的全文索引
    * 对数据类型和SQL有很好的支持，兼容性好，便于移植
    * 支持嵌入式数据库、内存数据库、只读数据库等；
    * 能够通过浏览器操控数据库。（使用了一下还是没有sql工具好用）

### 3.定位

* 此外由于 H2 支持内存模式，因此在进行单元测试的时候非常适合内存数据库。
* 另外由于 H2 文件体积非常小，安装、启动非常简单，且支持全文检索等高级特性，因此在一些简单场景下使用 H2 也能够快速建立起应用。
* H2 可以作为嵌入式数据库，数据库读性能要优于 SQLite，H2 官方提供的一个建议：可以在需要时使用时替换 SQLite。

### 4.单元测试示例


* 定义支持枚举
  * 支持数据库
    Compatibility modes for IBM DB2, Apache Derby, HSQLDB, MS SQL Server, MySQL, Oracle, and PostgreSQL.
```
public enum DBType {
	MySQL("MySQL"), Oracle("Oracle"), PostgreSQL("PostgreSQL"),
	DB2("DB2"), Derby("Derby"), HSQLDB("HSQLDB"), MSSQLServer("MSSQLServer");

	private String value;

	DBType(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}

```
* 封装数据库

```
public class H2DataBase {
	private String tcpUser = "sa";
	private String tcpPwd = "123456";
	private String tcpPort = "9093";
	private String dbDirectory = "~";
	private String urlTemplate = "jdbc:h2:%s/%s/h2db;MODE=%s;CACHE_SIZE=%s;DATABASE_TO_LOWER=TRUE";
	//缓存大小默认为K 10M
	private int cacheSize = 1024 * 20;

	private Server server;

	private static final Logger LOGGER = LoggerFactory.getLogger(H2DataBase.class);

	public H2DataBase() {
		try {

			server = Server.createTcpServer("-tcp", "-ifNotExists", "-tcpAllowOthers",
					"-tcpPort", tcpPort, "-tcpPassword", tcpPwd, "-baseDir", dbDirectory).start();

			Class.forName("org.h2.Driver");
		} catch (Exception e) {
			LOGGER.error("H2DataBase start h2tcpServer error, port{}, dbDirectory:{}", tcpPort, dbDirectory, e);
		}

	}

	public Connection getConnection() {
		return getConnection(DBType.MySQL);
	}

	public Connection getConnection(DBType dbType) {
		Connection connection = null;
		String dbUrl = null;
		try {
			dbUrl = String.format(urlTemplate, server.getURL(), dbDirectory, dbType.toString(), cacheSize);
			System.out.println(dbUrl + "  user:" + tcpUser + ", pwd:" + tcpPwd);
			connection = DriverManager.getConnection(dbUrl, tcpUser, tcpPwd);
		} catch (Exception e) {
			LOGGER.error("getConnection error, dbUrl{} ", dbUrl, e);
		}
		return connection;
	}

    //...

}
```

* 封装数据源

```
public class H2DataSource implements DataSource {

	private static final Logger LOGGER = LoggerFactory.getLogger(H2DataSource.class);

	private H2DataBase h2DataBase;

	public static H2DataSource getInstance() {
		return H2DataSourceHoler.INSTANCE;
	}

	public static Connection getH2Connection() {
		return getH2Connection(DBType.MySQL);
	}

	public static Connection getH2Connection(DBType dbType) {
		return getInstance().h2DataBase.getConnection(dbType);
	}

	public static class H2DataSourceHoler {
		private static H2DataSource INSTANCE = new H2DataSource();
	}

	private H2DataSource() {
		h2DataBase = new H2DataBase();
	}
	...

}

```

* 测试

```
public class H2DataSourceTest {
	private static final String CREATE_TABLE = "DROP TABLE h2test_tb IF EXISTS; create table h2test_tb(id integer,name VARCHAR(22) )";
	private static final String INSERT_SQL = "INSERT INTO h2test_tb VALUES(%s,'%s')";
	private static final String SELECT_SQL = "SELECT id,name from h2test_tb where id = %s";
	private static final String DELETE_SQL = "DELETE FROM h2test_tb WHERE id = %s";

	@Test
	public void testCreateTable() throws SQLException {
		Statement createStatement = H2DataSource.getH2Connection().createStatement();
		long f1 = createStatement.executeUpdate(CREATE_TABLE);
		Assert.assertEquals(f1, 0);
	}

	@Test
	public void testInsert() throws SQLException {
		int i = 1;
		String value = "h2test";
		testCreateTable();
		Statement insertStatement = H2DataSource.getH2Connection().createStatement();
		String insertSql = String.format(INSERT_SQL, i, value);
		long f2 = insertStatement.executeUpdate(insertSql);
		System.out.println("testInsert：" + f2);
		Assert.assertEquals(f2, 1);
	}

	@Test
	public void testSelect() throws SQLException {
		int i = 1;
		String value = "h2test";
		testCreateTable();
		Statement insertStatement = H2DataSource.getH2Connection().createStatement();
		String insertSql = String.format(INSERT_SQL, i, value);
		insertStatement.executeUpdate(insertSql);
		String selectSql = String.format(SELECT_SQL, i);
		PreparedStatement prepareStatement = H2DataSource.getInstance().getConnection().prepareStatement(selectSql);
		// 发送SQL 返回一个ResultSet
		ResultSet rs = prepareStatement.executeQuery();
		// 编历结果集
		int id = 0;
		String name = "";
		while (rs.next()) {
			id = rs.getInt(1); // 从1开始
			name = rs.getString(2);
			System.out.println("id:" + id + " name:" + name);
		}
		Assert.assertEquals(value, name);
	}

	@Test
	public void testDelete() throws SQLException {
		int i = 1;
		String value = "h2test";
		testCreateTable();
		Statement insertStatement = H2DataSource.getH2Connection().createStatement();
		String insertSql = String.format(INSERT_SQL, i, value);
		insertStatement.executeUpdate(insertSql);
		String selectSql = String.format(SELECT_SQL, i);
		PreparedStatement prepareStatement = H2DataSource.getInstance().getConnection().prepareStatement(selectSql);
		// 发送SQL 返回一个ResultSet
		ResultSet rs = prepareStatement.executeQuery();
		// 编历结果集
		int id = 0;
		String name = "";
		while (rs.next()) {
			id = rs.getInt(1); // 从1开始
			name = rs.getString(2);
			System.out.println("id:" + id + " name:" + name);
		}

		String deleteSql = String.format(DELETE_SQL, i);
		insertStatement.executeUpdate(deleteSql);
		prepareStatement = H2DataSource.getInstance().getConnection().prepareStatement(selectSql);
		// 发送SQL 返回一个ResultSet
		rs = prepareStatement.executeQuery();
		int j = 0;
		while (rs.next()) {
			j++;
		}
		Assert.assertEquals(0, j);

	}

}

```