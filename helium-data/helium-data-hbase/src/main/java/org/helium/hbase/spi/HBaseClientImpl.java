package org.helium.hbase.spi;

import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Table;
import org.helium.hbase.HBaseClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by lvmingwei on 16-6-22.
 */
public class HBaseClientImpl implements HBaseClient {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private Properties config;

    private Connection connection;

    protected HBaseClientImpl(Connection connection, Properties config) {
        this.connection = connection;
        this.config = config;
    }


    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public Admin getAdmin() {
        try {
            return getConnection().getAdmin();
        } catch (IOException e) {
            LOGGER.error("getAdmin Exception", e);
        }
        return null;
    }

    @Override
    public boolean existTable(String table) {
        try {
            return getConnection().getAdmin().tableExists(TableName.valueOf(table));
        } catch (IOException e) {
            LOGGER.error("existTable Exception:{}", table, e);
        }
        return false;
    }

    @Override
    public boolean existTable(String namespace, String table) {
        try {
            return getConnection().getAdmin().tableExists(TableName.valueOf(namespace, table));
        } catch (IOException e) {
            LOGGER.error("existTable Exception:{}{}", namespace, table, e);
        }
        return false;
    }

    @Override
    public boolean existNameSpace(String namespace) {
        NamespaceDescriptor namespaceDescriptor = null;
        try {
            namespaceDescriptor = getConnection().getAdmin().getNamespaceDescriptor(namespace);
            if (namespaceDescriptor == null) {
                return false;
            }
            return true;
        } catch (NamespaceNotFoundException e) {
            LOGGER.warn("NamespaceNotFound Is:{}", namespace);
            return false;
        } catch (IOException e) {
            LOGGER.error("existNameSpace Exception:{}", namespace, e);
        }

        return false;
    }

    @Override
    public boolean createNameSpace(String namespace) {
        NamespaceDescriptor namespaceDescriptor = NamespaceDescriptor.create(namespace).build();
        try {
            getConnection().getAdmin().createNamespace(namespaceDescriptor);
            return true;
        } catch (NamespaceExistException e) {
            LOGGER.warn("NamespaceExist Is:{}", namespace);
        } catch (IOException e) {
            LOGGER.error("createNameSpace Exception:{}", namespace, e);
        }
        return false;
    }

    @Override
    public boolean createTable(String table, String[] cfs) {

        try {
            HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(table));
            // 列族相关信息
            Arrays.stream(cfs).forEach(cf -> {
                HColumnDescriptor columnDescriptor = new HColumnDescriptor(cf);
                columnDescriptor.setMaxVersions(1);
                // 增加列族
                tableDescriptor.addFamily(columnDescriptor);
            });
            // 创建表
            getConnection().getAdmin().createTable(tableDescriptor);
            return true;
        } catch (TableExistsException e) {
            LOGGER.warn("TableExists Is:{}", table);
        } catch (IOException e) {
            LOGGER.error("createTable Exception:{}", table, e);
        }
        return false;
    }

    @Override
    public boolean createTable(String namespace, String table, String[] cfs) {

        try {
            HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(namespace, table));
            // 列族相关信息
            Arrays.stream(cfs).forEach(cf -> {
                HColumnDescriptor columnDescriptor = new HColumnDescriptor(cf);
                columnDescriptor.setMaxVersions(1);
                // 增加列族
                tableDescriptor.addFamily(columnDescriptor);
            });
            // 创建表
            getConnection().getAdmin().createTable(tableDescriptor);
            return true;
        } catch (TableExistsException e) {
            LOGGER.warn("TableExists Is:{}", table);
        } catch (IOException e) {
            LOGGER.error("createTable Exception:{}:{}", namespace, table, e);
        }
        return false;
    }

    /**
     * must be close
     *
     * @param namespace
     * @param name
     * @return
     */
    @Override
    public Table getTable(String namespace, String name) {
        try {
            TableName tableName = TableName.valueOf(namespace, name);
            Table table = connection.getTable(tableName);
            return table;
        } catch (Exception e) {
            LOGGER.error("getTable Exception:{}:{}", namespace, name, e);
        }
        return null;
    }

    /**
     * must be close
     *
     * @param name
     * @return
     */
    @Override
    public Table getTable(String name) {
        try {
            TableName tableName = TableName.valueOf(name);
            Table table = connection.getTable(tableName);
            return table;
        } catch (Exception e) {
            LOGGER.error("getTable Exception:{}", name, e);
        }
        return null;
    }

}