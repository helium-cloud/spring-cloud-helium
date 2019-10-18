package org.helium.redis.widgets.redis.client.pipeline;

import com.feinno.superpojo.SuperPojoManager;
import com.feinno.superpojo.util.StringUtils;
import org.helium.framework.BeanContext;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.SetterNode;
import org.helium.redis.widgets.redis.client.CFG_RedisSentinels;
import org.helium.redis.widgets.redis.client.sentinel.PropertyItem;
import org.helium.redis.widgets.redis.client.sentinel.RoleConfig;
import org.helium.redis.widgets.redis.client.sentinel.RoleConfigItem;

import java.util.ArrayList;
import java.util.List;

public class PipelineClusterLoader implements FieldLoader {

    @Override
    public Object loadField(SetterNode node) {
        try {

            String nodeInnerText = node.getInnerText();

            if (StringUtils.isNullOrEmpty(nodeInnerText)) {
                throw new Exception("nodeInnerText should not null");
            }

            List<CFG_RedisSentinels> list = null;
            String roleName = null;


            ConfigProvider configProvider = (ConfigProvider) BeanContext.getContextService().getService(ConfigProvider.class);
            String text = configProvider.loadText(nodeInnerText);
            RoleConfig roleConfig = SuperPojoManager.parseXmlFrom(text, RoleConfig.class);
            roleName = roleConfig.getRoleName();
            list = getItemsFromFileConfig(roleConfig);

            // RedisSentinelClient2 client = RedisSentinelManager2.INSTANCE.getRedisClient(roleName, list);

            PiplineSentinel client=new PiplineSentinel();
            PipelineCluster cluster = new PipelineCluster(client);
            return cluster;
        } catch (Exception ex) {
            throw new RuntimeException("SentinelClusterLoader2 loadField RedisCluster failed:", ex);
        }


    }

    private List<CFG_RedisSentinels> getItemsFromFileConfig(RoleConfig roleConfig) {
        List<CFG_RedisSentinels> result = new ArrayList<>();

        for (RoleConfigItem configItem : roleConfig.getItems()) {
            CFG_RedisSentinels item = new CFG_RedisSentinels();

            item.setRoleName(roleConfig.getRoleName());
            item.setPolicy(configItem.getPolicy());
            item.setNodeOrder(configItem.getNodeOrder());
            item.setAddrs(configItem.getMasterAddr());

            String propertiesExt = buildPropertyString(configItem.getPropertyItems());

            item.setPropertiesExt(propertiesExt);

            item.setWeight(configItem.getWeight());
            item.setEnabled(configItem.getEnabled());
            item.setMasterName(configItem.getMasterName());
            result.add(item);
        }

        return result;
    }

    private String buildPropertyString(List<PropertyItem> items) {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (PropertyItem item : items) {
            if (count > 0) {
                sb.append("\n");
            }

            sb.append(item.getKey() + "=" + item.getValue());
            ++count;
        }

        String result = sb.toString();
        return result;
    }
}
