package zconfig.loaders;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.SuperPojoManager;
import com.feinno.superpojo.util.ProtoGenericsUtils;
import org.helium.framework.configuration.Environments;
import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.SetterNode;
import org.helium.util.StringUtils;
import zconfig.configuration.ConfigDataManager;
import zconfig.configuration.ConfigurationManager;
import zconfig.configuration.HAConfigurator;
import zconfig.configuration.WorkerAgentHA;
import zconfig.configuration.args.ConfigTable;
import zconfig.configuration.args.ConfigTableItem;
import zconfig.configuration.args.ConfigText;
import zconfig.configuration.args.ConfigType;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * Created by liufeng on 2016/1/8.
 */
public class ConfigurationLoader implements FieldLoader {

    @Override
    public Object loadField(SetterNode node) {
        return null;
    }

    @Override
    public Object loadField(SetterNode node, Field field){

        String centerUrl = Environments.getVar(HAConfigurator.CENTER_URL_KEY);

        if (!StringUtils.isNullOrEmpty(centerUrl)) {
            //connect HACenter
            WorkerAgentHA.init();

            Class clazz = field.getType();

            if (clazz == ConfigTable.class) {
                return processTable(node, field);
            } else if (clazz == ConfigText.class) {
                return processText(node, field);
            } else {
                throw new UnsupportedOperationException("Unsupported type!!!");
            }
        }
        else
        {
            return null;
        }
    }

    private <K, V extends ConfigTableItem> ConfigTable<K, V>  processTable(SetterNode node, Field field)
    {
        String tableName = node.getInnerText();

        try {
            Class classKey = ProtoGenericsUtils.getGenericsClass(field, 0);
            Class classValue = ProtoGenericsUtils.getGenericsClass(field, 1);

            ConfigTable<K, V> table = ConfigDataManager.getConfigTable(tableName, ConfigType.TABLE);

            if(table == null) {
                table = ConfigurationManager.getInstance().loadTable(classKey, classValue, tableName);
            }

            return table;
        }
        catch (Exception ex)
        {
            throw new UnsupportedOperationException(ex.getMessage() + ", tableName: " + tableName);
        }
    }

    private <V> ConfigText<V> processText(SetterNode node, Field field)
    {
        String path = node.getInnerText();

        try {
            Class classValue = ProtoGenericsUtils.getGenericsClass(field, 0);

            ConfigText<V> configText = new ConfigText<V>(path, classValue);

            String strResult = ConfigDataManager.getConfigText(path, ConfigType.TEXT);

            if (StringUtils.isNullOrEmpty(strResult)) {
                strResult = ConfigurationManager.getInstance().loadText(path);
            }

            V instance = (V)classValue.newInstance();

            if (instance instanceof Properties)
            {
                Properties prop = new Properties();
                StringReader reader = new StringReader(strResult);

                prop.load(reader);

                configText.setValue((V)prop);

                return configText;
            }
            else if (instance instanceof SuperPojo)
            {
                SuperPojoManager.parseXmlFrom(strResult, instance);

                configText.setValue(instance);

                return configText;
            }
            else if (instance instanceof String)
            {
                configText.setValue((V)strResult);
                return configText;
            }
            else
            {
                throw new UnsupportedOperationException("Unsupported type!");
            }
        }
        catch (Exception ex)
        {
            throw new UnsupportedOperationException(ex.getMessage() + ", path: " + path);
        }
    }
}
