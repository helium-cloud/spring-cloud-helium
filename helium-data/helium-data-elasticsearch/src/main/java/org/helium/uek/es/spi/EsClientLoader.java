package org.helium.uek.es.spi;


import com.alibaba.fastjson.JSONObject;
import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.SetterNode;
import org.helium.framework.entitys.SetterNodeLoadType;
import org.helium.uek.es.EsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EsClientLoader
 *
 */
public class EsClientLoader implements FieldLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(EsClientManager.class);

    @Override
    public Object loadField(SetterNode node) {
        EsClient esClient = null;
        try {
            String configName = node.getInnerText();
            String configStr = node.getValue();
            SetterNodeLoadType loadType = node.getLoadType();
            switch (loadType) {
                //配置中心或者value加载
                case CONFIG_VALUE:
                    esClient = EsClientManager.INSTANCE.getEsClient(configName, configStr);
                    break;
                //动态加载
                case CONFIG_DYNAMIC:
                    esClient = EsClientManager.INSTANCE.getEsClient(configName, configStr);
                    break;
                //helium加载
                case CONFIG_PROVIDE:
                case UNKNOWN:
                    esClient = EsClientManager.INSTANCE.getEsClient(configName);
                    break;
                default:
                    esClient = EsClientManager.INSTANCE.getEsClient(configName);
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("loadField.{},", JSONObject.toJSONString(node), e);
        }
        return esClient;
    }
}
