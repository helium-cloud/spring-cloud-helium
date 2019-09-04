package org.helium.database.spi;

import org.helium.database.ConnectionString;

import javax.sql.DataSource;

/**
 * Created by Coral on 5/5/15.
 */
public interface DataSourceProvider {
	DataSource getDataSource(ConnectionString connStr);
}
