package org.helium.redis.widgets.redis.client;

/**
 * Created by yibo on 2017-6-9.
 */
public class CFG_RedisSentinels implements Comparable {

    private int id;// id, 仅仅是个id. 用来做个主键什么的.

    private String roleName = "";// 角色名称, 用来区分业务

    private String masterName = "";// 同一个业务角色的同一个分组下, master 应该只有1个..

    private String policy = "";

    private int nodeOrder;

    private String addrs = "";

    private String propertiesExt = "";

    private int weight;

    private int enabled;


    public CFG_RedisSentinels() {

    }

    public int getId() {
        return id;
    }

    public CFG_RedisSentinels setId(int id) {
        this.id = id;
        return this;
    }


    public String getRoleName() {
        return roleName;
    }

    public CFG_RedisSentinels setRoleName(String roleName) {
        this.roleName = roleName;
        return this;
    }

    public String getMasterName() {
        return masterName;
    }

    public CFG_RedisSentinels setMasterName(String masterName) {
        this.masterName = masterName;
        return this;
    }

    public String getPolicy() {
        return policy;
    }

    public CFG_RedisSentinels setPolicy(String policy) {
        this.policy = policy;
        return this;
    }

    public int getNodeOrder() {
        return nodeOrder;
    }

    public CFG_RedisSentinels setNodeOrder(int nodeOrder) {
        this.nodeOrder = nodeOrder;
        return this;
    }

    public String getAddrs() {
        return addrs;
    }

    public CFG_RedisSentinels setAddrs(String addrs) {
        this.addrs = addrs;
        return this;
    }

    public String getPropertiesExt() {
        return propertiesExt;
    }

    public CFG_RedisSentinels setPropertiesExt(String propertiesExt) {
        this.propertiesExt = propertiesExt;
        return this;
    }

    public int getWeight() {
        return weight;
    }

    public CFG_RedisSentinels setWeight(int weight) {
        this.weight = weight;
        return this;
    }

    public int getEnabled() {
        return enabled;
    }

    public CFG_RedisSentinels setEnabled(int enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || !(o instanceof CFG_RedisSentinels))
            return false;

        CFG_RedisSentinels that = (CFG_RedisSentinels) o;

        if (enabled != that.enabled)
            return false;
        if (nodeOrder != that.nodeOrder)
            return false;
        if (id != that.id)
            return false;
        if (weight != that.weight)
            return false;
        if (!policy.equals(that.policy))
            return false;
        if (!roleName.equals(that.roleName))
            return false;
        if (!addrs.equals(that.addrs))
            return false;

        return true;
    }


    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + roleName.hashCode();
        result = 31 * result + policy.hashCode();
        result = 31 * result + nodeOrder;
        result = 31 * result + addrs.hashCode();
        result = 31 * result + weight;
        result = 31 * result + enabled;
        return result;
    }


    @Override
    public int compareTo(Object o) {

        if (o == null || !(o instanceof CFG_RedisSentinels))
            return 0;
        CFG_RedisSentinels other = (CFG_RedisSentinels) o;
        return this.getNodeOrder() - other.getNodeOrder();
    }


    public boolean isEnabled() {
        return 1 == getEnabled();
    }
}