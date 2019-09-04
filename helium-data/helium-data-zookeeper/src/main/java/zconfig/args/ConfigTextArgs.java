package zconfig.args;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

/**
 * Created by liufeng on 2017/8/10.
 */
public class ConfigTextArgs extends SuperPojo {
    @Field(id = 1)
    private String configKey;

    @Field(id = 2)
    private String configText;

    @Field(id = 3)
    private String version;

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigText() {
        return configText;
    }

    public void setConfigText(String configText) {
        this.configText = configText;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
