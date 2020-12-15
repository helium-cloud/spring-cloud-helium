package org.helium.plugin.mybatis;

import com.zaxxer.hikari.HikariDataSource;
import org.helium.plugin.mybatis.model.ConfigModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MultipleDataSource extends AbstractRoutingDataSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultipleDataSource.class);

    private static final ThreadLocal<String> DATA_SOURCE_POOL = new ThreadLocal<>();

    private static final Map<Object, Object> DATA_SOURCE_CACHE = new ConcurrentHashMap<>();

    @Override
    protected String determineCurrentLookupKey() {
        return DATA_SOURCE_POOL.get();
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public DataSource determineTargetDataSource() {
        return (DataSource) DATA_SOURCE_CACHE.get(DATA_SOURCE_POOL.get());
    }


    @Override
    public void setDefaultTargetDataSource(Object defaultTargetDataSource) {
        super.setDefaultTargetDataSource(DATA_SOURCE_CACHE.get(DATA_SOURCE_POOL.get()));
    }

    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        super.setTargetDataSources(DATA_SOURCE_CACHE);
    }

    public static void updateCurrentDataSource(String dataSourceName) {
        DATA_SOURCE_POOL.set(dataSourceName);
    }

    public static void removeCurrentDataSource() {
        DATA_SOURCE_POOL.remove();
    }


    /**
     * 创建数据源
     *
     * @param dataSourceName 数据源名称
     * @param config         数据源配置
     */
    public static void setDataSource(String dataSourceName, ConfigModel config) {
        DataSource dataSource = (DataSource) DATA_SOURCE_CACHE.get(dataSourceName);
        Properties data = config.getData();
        if (Objects.isNull(dataSource)) {
            LOGGER.info("当前配置环境为[{}]，配置加载地址为[{}]，开始创建[{}]的配置[{}]的数据源...", config.getActive(), config.getPosition(), config.getGroup(), config.getKey());
            if (CollectionUtils.isEmpty(data)) {
                //throw new Exception("数据源配置[{}]错误！");
            }
            String driverClassname = data.getProperty("driver-class-name");
            if (Objects.isNull(driverClassname) || driverClassname.length() == 0) {
                //throw new Exception("数据源[driver-class-name]配置不能为空！");
            }
            String url = data.getProperty("url");
            if (Objects.isNull(url) || url.length() == 0) {
                //throw new BusinessException("数据源[url]配置不能为空！");
            }
            String username = data.getProperty("username");
            if (Objects.isNull(username) || username.length() == 0) {
                //throw new BusinessException("数据源[username]配置不能为空！");
            }
            String password = data.getProperty("password");
            if (Objects.isNull(password) || password.length() == 0) {
                //throw new BusinessException("数据源[username]配置不能为空！");
            }
            dataSource = DataSourceBuilder.create().type(HikariDataSource.class)
                    .driverClassName(driverClassname)
                    .url(url)
                    .username(username)
                    .password(password).build();
            DATA_SOURCE_CACHE.put(dataSourceName, dataSource);
            LOGGER.info("[{}]的配置[{}]的数据源创建完成！", config.getGroup(), config.getKey());
        }
    }

    public static MultipleDataSource instance() {
        return SpringContextHelper.getBean(MultipleDataSource.class);
    }
}

