package org.helium.kafka.spi.producer;


import com.alibaba.fastjson.JSONObject;
import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.SetterNode;
import org.helium.framework.entitys.SetterNodeLoadType;
import org.helium.kafka.UkProducer;
import org.helium.kafka.spi.consumer.UkConsumerLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * kafka生产者loader
 */
public class UkProducerLoader implements FieldLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(UkConsumerLoader.class);
    @Override
    public Object loadField(SetterNode node) {


        UkProducer ukProducer = null;
        try {
            String configName = node.getInnerText();
            String configStr = node.getValue();
            SetterNodeLoadType loadType = node.getLoadType();
            switch (loadType) {
                //配置中心或者value加载
                case CONFIG_VALUE:
                    ukProducer = UkProducerManager.INSTANCE.getKafkaProducer(configName, configStr);
                    break;
                //动态加载
                case CONFIG_DYNAMIC:
                    ukProducer = UkProducerManager.INSTANCE.getAndUpdateKafkaProducer(configName, configStr);
                    break;
                //helium加载
                case CONFIG_PROVIDE:
                case UNKNOWN:
                    ukProducer = UkProducerManager.INSTANCE.getKafkaProducer(configName);
                    break;
                default:
                    ukProducer = UkProducerManager.INSTANCE.getKafkaProducer(configName);
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("loadField.{},", JSONObject.toJSONString(node), e);
        }
        return ukProducer;

    }
}
