package org.helium.hbase;

import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Table;
import org.helium.framework.annotations.FieldLoaderType;
import org.helium.hbase.spi.HBaseClientFieldLoader;

@FieldLoaderType(loaderType = HBaseClientFieldLoader.class)
public interface HBaseClient{
    Connection getConnection();
    Admin getAdmin() throws Exception;
    Table getTable(String namespace, String name);
    Table getTable(String name);
    boolean createNameSpace(String namespace);
    boolean createTable(String table, String[] cfs);
    boolean createTable(String namespace, String table, String[] cfs);
    boolean existTable(String table);
    boolean existTable(String namespace, String table);
    boolean existNameSpace(String namespace);
}