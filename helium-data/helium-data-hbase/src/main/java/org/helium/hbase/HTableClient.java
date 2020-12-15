package org.helium.hbase;

import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Table;
import org.helium.framework.annotations.FieldLoaderType;
import org.helium.hbase.spi.HTableFieldLoader;

/**
 * Created by lvmingwei on 16-6-22.
 */
@FieldLoaderType(loaderType = HTableFieldLoader.class)
public interface HTableClient extends Table {
	Connection getConnection();
}
