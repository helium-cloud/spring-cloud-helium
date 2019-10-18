package org.helium.redis.widgets.redis.client.sentinel;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

import java.util.List;

public class RoleConfig extends SuperPojo {

    @Field(id=1,type= NodeType.ATTR)
    private String roleName;

    @Field(id = 2)
    private List<RoleConfigItem> items;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<RoleConfigItem> getItems() {
        return items;
    }

    public void setItems(List<RoleConfigItem> items) {
        this.items = items;
    }


}
