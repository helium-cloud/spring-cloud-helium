package zconfig.args;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import zconfig.configuration.args.ConfigType;
import zconfig.enums.LoadDataMode;

/**
 * Created by liufeng on 2017/8/14.
 */
public class ConfigUpdateNotifyArgs extends SuperPojo {
    @Field(id = 1)
    private String configKey;

    @Field(id = 2)
    private ConfigType configType;

    @Field(id = 3)
    private LoadDataMode loadDataMode;

    @Field(id = 4)
    private String serviceName;

    @Field(id = 5)
    private String machineAddress;

    @Field(id = 6)
    private String lastReadTime;

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public ConfigType getConfigType() {
        return configType;
    }

    public void setConfigType(ConfigType configType) {
        this.configType = configType;
    }

    public LoadDataMode getLoadDataMode() {
        return loadDataMode;
    }

    public void setLoadDataMode(LoadDataMode loadDataMode) {
        this.loadDataMode = loadDataMode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMachineAddress() {
        return machineAddress;
    }

    public void setMachineAddress(String machineAddress) {
        this.machineAddress = machineAddress;
    }

    public String getLastReadTime() {
        return lastReadTime;
    }

    public void setLastReadTime(String lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    public String dumpObject() {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("configKey:%s|", this.configKey));
        if (configType != null) {
            sb.append(String.format("configType:%s|", this.configType.toString()));
        }

        if (loadDataMode != null) {
            sb.append(String.format("loadDataMode:%s|", this.loadDataMode.getName()));
        }

        sb.append(String.format("serviceName:%s|", this.serviceName));
        sb.append(String.format("machineAddress:%s|", this.machineAddress));
        sb.append(String.format("lastReadTime:%s|", this.lastReadTime));

        return sb.toString();
    }
}
