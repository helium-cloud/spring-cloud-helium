package org.helium.database.pooled;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

/**
 * Created by john.y on 2017-9-1.
 */

public class DatabaseNode extends SuperPojo {
    @Field(id = 1, name = "id", type = NodeType.ATTR)
    private int id;

    @Field(id = 2, name = "name", type = NodeType.ATTR)
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
