package org.helium.hbase.test;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.helium.framework.annotations.FieldSetter;
import org.helium.framework.annotations.ServiceImplementation;
import org.helium.hbase.HTableClient;

import java.io.IOException;

/**
 * Created by lvmingwei on 16-6-22.
 */
@ServiceImplementation
public class HBaseServerTestImpl implements HBaseServerTest {

    @FieldSetter("RCS_T1")
    private HTableClient hTable;

    @Override
    public void ping() {

		try {
			Result result = hTable.get(new Get("RowKey".getBytes()));

			byte[] row = result.getRow();
		} catch (IOException e) {
			e.printStackTrace();
		}


		System.out.println("Ping");
    }

    public HTableClient getHTableClient() {
        return hTable;
    }
}
