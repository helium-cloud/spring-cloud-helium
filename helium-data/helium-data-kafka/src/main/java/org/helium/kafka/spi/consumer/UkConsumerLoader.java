package org.helium.kafka.spi.consumer;

import com.alibaba.fastjson.JSONObject;
import org.helium.database.ConnectionString;
import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.SetterNode;
import org.helium.framework.entitys.SetterNodeLoadType;
import org.helium.kafka.UkConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * kafka消费者loader
 */
public class UkConsumerLoader implements FieldLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(UkConsumerLoader.class);

    @Override
    public Object loadField(SetterNode node) {

        UkConsumer ukConsumer = null;
        try {
            String configName = node.getInnerText();
            String configStr = node.getValue();
            SetterNodeLoadType loadType = node.getLoadType();
            ConnectionString cs = null;
            switch (loadType) {
                //配置中心或者value加载
                case CONFIG_VALUE:
                    ukConsumer = UkConsumerManager.INSTANCE.getKafkaConsumer(configName, configStr);
                    break;
                //动态加载
                case CONFIG_DYNAMIC:
                    ukConsumer = UkConsumerManager.INSTANCE.getAndUpdateKafkaConsumer(configName, configStr);
                    break;
                //helium加载
                case CONFIG_PROVIDE:
                case UNKNOWN:
                    ukConsumer = UkConsumerManager.INSTANCE.getKafkaConsumer(configName);
                    break;
                default:
                    ukConsumer = UkConsumerManager.INSTANCE.getKafkaConsumer(configName);
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("loadField.{},", JSONObject.toJSONString(node), e);
        }
        return ukConsumer;
    }
}
