package org.helium.fastdfs.spi;

import com.alibaba.fastjson.JSONObject;
import org.helium.fastdfs.FastDFSClient;
import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.SetterNode;
import org.helium.framework.entitys.SetterNodeLoadType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lvmingwei on 16-1-4.
 */
public class FastDFSFieldLoader implements FieldLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(FastDFSFieldLoader.class);
    @Override
    public Object loadField(SetterNode node) {


        FastDFSClient fastDFSClient = null;
        try {
            String configName = node.getInnerText();
            String configStr = node.getValue();
            SetterNodeLoadType loadType = node.getLoadType();
            switch (loadType) {
                //配置中心或者value加载
                case CONFIG_VALUE:
                    fastDFSClient = FastDFSManager.INSTANCE.getFastDFSClient(configName, configStr);
                    break;
                //动态加载
                case CONFIG_DYNAMIC:
                    fastDFSClient = FastDFSManager.INSTANCE.getAndUpdateFastDFSClient(configName, configStr);
                    break;
                //helium加载
                case CONFIG_PROVIDE:
                case UNKNOWN:
                    fastDFSClient = FastDFSManager.INSTANCE.getFastDFSClient(configName);
                    break;
                default:
                    fastDFSClient = FastDFSManager.INSTANCE.getFastDFSClient(configName);
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("loadField.{},", JSONObject.toJSONString(node), e);
        }
        return fastDFSClient;

    }
}
