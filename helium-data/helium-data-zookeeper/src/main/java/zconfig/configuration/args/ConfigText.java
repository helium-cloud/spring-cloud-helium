package zconfig.configuration.args;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.SuperPojoManager;
import zconfig.configuration.ConfigurationManager;

import java.io.StringReader;
import java.util.Properties;

/**
 * Created by liufeng on 2016/1/8.
 */
public class ConfigText<V> {

    private String path;
    private Class classValue;

    public ConfigText(String path, Class classValue)
    {
        this.path = path;
        this.classValue = classValue;
    }

    private V value;

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void addEvent(final ConfigUpdateAction<V> updateCallback) throws ConfigurationException
    {
        ConfigurationManager.getInstance().callBackConfigData(path, new ConfigUpdateAction<String>() {
            @Override
            public void run(String strResult) throws Exception {
                if (value instanceof Properties)
                {
                    Properties prop = new Properties();
                    StringReader reader = new StringReader(strResult);

                    prop.load(reader);

                    if (updateCallback != null)
                    {
                        updateCallback.run((V)prop);
                    }
                }
                else if (value instanceof SuperPojo)
                {
                    V entity = (V)classValue.newInstance();

                    SuperPojoManager.parseXmlFrom(strResult, entity);

                    if (updateCallback != null)
                    {
                        updateCallback.run((V)entity);
                    }
                }
                else if (value instanceof String)
                {
                    if (updateCallback != null)
                    {
                        updateCallback.run((V)strResult);
                    }
                }
            }
        });
    }
}
