package org.helium.data.h2;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * H2DataBase连接
 *
 * @author wuhao
 * @createTime 2021-06-10 18:52:00
 */
public class H2DataBase {
	private String tcpUser = "sa";
	private String tcpPwd = "123456";
	private String tcpPort = "9093";
	private String dbDirectory = "~";
	private String urlTemplate = "jdbc:h2:%s%s/h2db;MODE=%s;CACHE_SIZE=%s;DATABASE_TO_LOWER=TRUE";
	//缓存大小默认为K 10M
	private int cacheSize = 1024 * 20;

	private Server server = null;

	private Connection connection = null;

	private static final Logger LOGGER = LoggerFactory.getLogger(H2DataBase.class);

	public H2DataBase() {
		init(DBType.MySQL, true);
	}

	public H2DataBase(DBType dbType, boolean runServer) {
		init(dbType, runServer);
	}

	private void init(DBType dbType, boolean runServer) {
		try {
			String dbUrl = null;
			if (runServer){
				server = Server.createTcpServer("-tcp", "-ifNotExists", "-tcpAllowOthers",
						"-tcpPort", tcpPort, "-tcpPassword", tcpPwd, "-baseDir", dbDirectory).start();
				dbUrl = String.format(urlTemplate, server.getURL() + "/", dbDirectory, dbType.toString(), cacheSize);
			} else {
				dbUrl = String.format(urlTemplate, "", dbDirectory, dbType.toString(), cacheSize);
			}

			Class.forName("org.h2.Driver");
			System.out.println(dbUrl + "  user:" + tcpUser + ", pwd:" + tcpPwd);
			connection = DriverManager.getConnection(dbUrl, tcpUser, tcpPwd);

		} catch (Exception e) {
			LOGGER.error("H2DataBase start h2tcpServer error, port{}, dbDirectory:{}", tcpPort, dbDirectory, e);
		}
	}

	public Connection getConnection() {
		return connection;
	}

	public String getTcpUser() {
		return tcpUser;
	}

	public void setTcpUser(String tcpUser) {
		this.tcpUser = tcpUser;
	}

	public String getTcpPwd() {
		return tcpPwd;
	}

	public void setTcpPwd(String tcpPwd) {
		this.tcpPwd = tcpPwd;
	}

	public String getTcpPort() {
		return tcpPort;
	}

	public void setTcpPort(String tcpPort) {
		this.tcpPort = tcpPort;
	}

	public String getDbDirectory() {
		return dbDirectory;
	}

	public void setDbDirectory(String dbDirectory) {
		this.dbDirectory = dbDirectory;
	}

	public String getUrlTemplate() {
		return urlTemplate;
	}

	public void setUrlTemplate(String urlTemplate) {
		this.urlTemplate = urlTemplate;
	}

	public int getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}


}