package zconfig.configuration.table;


import zconfig.configuration.args.ConfigTableField;
import zconfig.configuration.args.ConfigTableItem;

/**
 * Created by liufeng on 2016/2/6.
 */
public class URCS_RedisSentinels extends ConfigTableItem {
    @ConfigTableField(value = "Id", isKeyField = true)
    private int id;// id, 仅仅是个id. 用来做个主键什么的.

    @ConfigTableField("RoleName")
    private String roleName = "";// 角色名称, 用来区分业务

    @ConfigTableField("MasterName")
    private String masterName = "";// 同一个业务角色的同一个分组下, master 应该只有1个..

     @ConfigTableField("Policy")
    private String policy = "";

    @ConfigTableField("NodeOrder")
    private int nodeOrder;

    @ConfigTableField("Addrs")
    private String addrs = "";

    @ConfigTableField("PropertiesExt")
    private String propertiesExt = "";

    @ConfigTableField("Weight")
    private int weight;

     @ConfigTableField("Enabled")
    private int enabled;

    public int getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getMasterName() {
        return masterName;
    }

    public String getPolicy() {
        return policy;
    }

    public int getNodeOrder() {
        return nodeOrder;
    }

    public String getAddrs() {
        return addrs;
    }

    public String getPropertiesExt() {
        return propertiesExt;
    }

    public int getWeight() {
        return weight;
    }

    public int getEnabled() {
        return enabled;
    }
}
