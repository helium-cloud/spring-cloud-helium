package org.helium.hbase.spi;

import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.SetterNode;

/**
 * Created by lvmingwei on 16-6-22.
 */
public class HTableFieldLoader implements FieldLoader {
    @Override
    public Object loadField(SetterNode node) {
        String tableName = node.getInnerText();
        return HBaseManager.INSTANCE.getHTableClient(tableName);
    }
}
