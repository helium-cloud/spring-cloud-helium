package org.helium.hbase.test;

import org.helium.framework.annotations.ServiceInterface;
import org.helium.hbase.HTableClient;
/**
 * Created by lvmingwei on 16-6-22.
 */

@ServiceInterface(id = "test:HBaseServerTest")
public interface HBaseServerTest {

    public void ping();

    public HTableClient getHTableClient();
}
