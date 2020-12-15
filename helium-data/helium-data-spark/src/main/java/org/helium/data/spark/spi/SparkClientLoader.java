package org.helium.data.spark.spi;


import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.SetterNode;

/**
 * spark loader
 */
public class SparkClientLoader implements FieldLoader {
    @Override
    public Object loadField(SetterNode node) {
        String sparkConf = node.getInnerText();
        return SparkClientManager.INSTANCE.getSparkClient(sparkConf);
    }
}
