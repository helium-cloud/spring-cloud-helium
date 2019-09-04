package org.helium.data.zookeeper.configCenter.loader;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.SuperPojoManager;
import com.feinno.superpojo.util.ProtoGenericsUtils;

import org.helium.data.zookeeper.configCenter.ConstantKey;
import org.helium.data.zookeeper.configCenter.entity.ZKConfigTable;
import org.helium.data.zookeeper.configCenter.entity.ZKConfigText;
import org.helium.data.zookeeper.configCenter.manager.ZKConfigDataManager;
import org.helium.data.zookeeper.configCenter.manager.ZKConfigurationManager;
import org.helium.data.zookeeper.configCenter.manager.ZKConfigurator;
import org.helium.framework.configuration.Environments;
import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.SetterNode;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zconfig.CentralizedManager;
import zconfig.configuration.args.ConfigTableItem;
import zconfig.configuration.args.ConfigType;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * 基于zk的动态配置中心类加载器
 * Created by yushaobo on 18-8-23.
 */
public class ZkConfigurationLoader implements FieldLoader {

    private static Logger LOGGER = LoggerFactory.getLogger(ZkConfigurationLoader.class);

    @Override
    public Object loadField(SetterNode node) {
        return null;
    }

    @Override
    public Object loadField(SetterNode node, Field field) {
        try {
            String zkHost = Environments.getVar(ConstantKey.ZK_CONFIG_HOSTS);

            if (!StringUtils.isNullOrEmpty(zkHost)) {
                CentralizedManager.getInstance().connect(zkHost);

                ZKConfigurator configurator = new ZKConfigurator(CentralizedManager.getInstance().getZooKeeperConnector());
                ZKConfigurationManager.getInstance().setConfigurator(configurator);

                Class clazz = field.getType();

                if (clazz == ZKConfigTable.class) {
                    return processTable(node, field);
                } else if (clazz == ZKConfigText.class) {
                    return processText(node, field);
                } else {
                    throw new UnsupportedOperationException("Unsupported type!!!");
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            LOGGER.error(String.format("ZkConfigurationLoader.loadField is error, %s", ex.getMessage()), ex);

            return null;
        }
    }

    /**
     * 从zk中获取table
     * @param node
     * @param field
     * @return
     */
    private <K, V extends ConfigTableItem> ZKConfigTable<K, V> processTable(SetterNode node, Field field) {
        String tableName = node.getInnerText();

        try {
            Class classKey = ProtoGenericsUtils.getGenericsClass(field, 0);
            Class classValue = ProtoGenericsUtils.getGenericsClass(field, 1);

            ZKConfigTable<K, V> table = ZKConfigDataManager.getConfigTable(tableName, ConfigType.TABLE);

            if (table == null) {
                table = ZKConfigurationManager.getInstance().loadTable(classKey, classValue, tableName);

                ZKConfigDataManager.putConfigTable(tableName, ConfigType.TABLE, table);
            }

            return table;
        } catch (Exception ex) {
            throw new UnsupportedOperationException(ex.getMessage() + ", tableName: " + tableName);
        }
    }

    private <V> ZKConfigText<V> processText(SetterNode node, Field field) {
        String path = node.getInnerText();

        try {
            Class classValue = ProtoGenericsUtils.getGenericsClass(field, 0);

            ZKConfigText<V> configText = new ZKConfigText<V>(path, classValue);

            String strResult = ZKConfigDataManager.getConfigText(path, ConfigType.TEXT);

            if (StringUtils.isNullOrEmpty(strResult)) {
                strResult = ZKConfigurationManager.getInstance().loadText(path);

                ZKConfigDataManager.putConfigText(path, ConfigType.TEXT, strResult);
            }

            V instance = (V) classValue.newInstance();

            if (instance instanceof Properties) {
                Properties prop = new Properties();
                StringReader reader = new StringReader(strResult);

                prop.load(reader);

                configText.setValue((V) prop);

                return configText;
            } else if (instance instanceof SuperPojo) {
                SuperPojoManager.parseXmlFrom(strResult, instance);

                configText.setValue(instance);

                return configText;
            } else if (instance instanceof String) {
                configText.setValue((V) strResult);
                return configText;
            } else {
                throw new UnsupportedOperationException("Unsupported type!");
            }
        } catch (Exception ex) {
            throw new UnsupportedOperationException(ex.getMessage() + ", path: " + path);
        }
    }
}
