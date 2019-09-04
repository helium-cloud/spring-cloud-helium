package org.helium.database.spi;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.helium.database.ConnectionString;

import javax.sql.DataSource;

/**
 * Created by Coral on 5/5/15.
 */
public class HikariDataSourceProvider implements DataSourceProvider {
    @Override
    public DataSource getDataSource(ConnectionString cs) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(cs.getDriverClassName());
        config.setJdbcUrl(cs.getJdbcUrl());
        config.setUsername(cs.getUser());
        config.setPassword(cs.getPassword());
        config.setConnectionTestQuery("select 1");


        cs.setPropInt("connectionTimeout", "", "30000", n -> config.setConnectionTimeout(n));
        cs.setPropInt("idleTimeout", "", "600000", n -> config.setIdleTimeout(n));
        cs.setPropInt("maxLifetime", "", "1800000", n -> config.setMaxLifetime(n));
        cs.setPropInt("minimumIdle", "", "2", n -> config.setMinimumIdle(n));

        cs.setPropInt("maximumPoolSize", "maxPoolSize", "8", n -> config.setMaximumPoolSize(n));
        cs.setPropInt("validationTimeout", "", "5000", n -> config.setValidationTimeout(n));

        cs.setPropInt("leakDetectionThreshold", "", "0", n -> config.setLeakDetectionThreshold(n));


        HikariDataSource ds = new HikariDataSource(config);
        return ds;
    }

}
