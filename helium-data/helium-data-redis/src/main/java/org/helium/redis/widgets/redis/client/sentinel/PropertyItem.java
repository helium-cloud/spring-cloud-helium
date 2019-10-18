package org.helium.redis.widgets.redis.client.sentinel;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

public class PropertyItem extends SuperPojo {

    @Field(id=1,type= NodeType.ATTR)
    private String key;

    @Field(id=2,type= NodeType.ATTR)
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
