package zconfig.args;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

/**
 * Created by liufeng on 2017/8/15.
 */
public class SubscribeDataArgs extends SuperPojo{
    @Field(id = 1)
    private String path;

    @Field(id = 2)
    private String serviceName;

    @Field(id = 3)
    private String machineAddress;

    @Field(id = 4)
    private String subscribeTime;

    @Field(id = 5)
    private String latestLoadDataTime;

    public SubscribeDataArgs(){
    }

    public SubscribeDataArgs(String path, String serviceName, String machineAddress, String subscribeTime)
    {
        this.path = path;
        this.serviceName = serviceName;
        this.machineAddress = machineAddress;
        this.subscribeTime = subscribeTime;
    }

    public SubscribeDataArgs(String path, String serviceName, String machineAddress, String subscribeTime, String latestLoadDataTime)
    {
        this.path = path;
        this.serviceName = serviceName;
        this.machineAddress = machineAddress;
        this.subscribeTime = subscribeTime;
        this.latestLoadDataTime = latestLoadDataTime;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public String getSubscribeTime() {
        return subscribeTime;
    }

    public void setSubscribeTime(String subscribeTime) {
        this.subscribeTime = subscribeTime;
    }

    public String getLatestLoadDataTime() {
        return latestLoadDataTime;
    }

    public void setLatestLoadDataTime(String latestLoadDataTime) {
        this.latestLoadDataTime = latestLoadDataTime;
    }
}
