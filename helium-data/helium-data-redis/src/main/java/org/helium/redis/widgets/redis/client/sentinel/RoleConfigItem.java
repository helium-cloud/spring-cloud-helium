package org.helium.redis.widgets.redis.client.sentinel;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Childs;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

import java.util.List;

public class RoleConfigItem extends SuperPojo {

    @Field(id=2,type= NodeType.ATTR)
    private String policy;


    @Field(id=3,type= NodeType.ATTR)
    private String site;

    @Field(id=4,type= NodeType.ATTR)
    private int nodeOrder;

    /**
     *  用；分开如：
     *  172.21.41.8:26404;172.21.41.30:26404;172.21.41.31:26404
     */
    @Field(id=5,type= NodeType.ATTR)
    private String masterAddr;

    @Field(id=7,type= NodeType.ATTR)
    private String masterName;

    @Field(id=8,type= NodeType.ATTR)
    private int weight;

    /**
     * 0: disable
     * 1:enable
     */
    @Field(id=9,type= NodeType.ATTR)
    private int enabled;

    @Childs(id = 10, parent = "propertiesExt", child = "propItem")
    private List<PropertyItem> propertyItems;

    public List<PropertyItem> getPropertyItems() {
        return propertyItems;
    }

    public void setPropertyItems(List<PropertyItem> propertyItems) {
        this.propertyItems = propertyItems;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getNodeOrder() {
        return nodeOrder;
    }

    public void setNodeOrder(int nodeOrder) {
        this.nodeOrder = nodeOrder;
    }

    public String getMasterAddr() {
        return masterAddr;
    }

    public void setMasterAddr(String masterAddr) {
        this.masterAddr = masterAddr;
    }


}
