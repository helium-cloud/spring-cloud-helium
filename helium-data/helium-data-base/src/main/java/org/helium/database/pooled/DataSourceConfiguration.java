package org.helium.database.pooled;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Childs;
import com.feinno.superpojo.annotation.Entity;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john.y on 2017-9-1.
 */

@Entity(name = "DataSourceConfiguration")
public class DataSourceConfiguration extends SuperPojo {
    @Field(id = 1, name = "name", type = NodeType.ATTR)
    private String name;

    @Childs(id = 2, child = "dataSource", parent = "dataSources")
    private List<DatabaseNode> dataSources = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DatabaseNode> getDataSources() {
        return dataSources;
    }

    public void setDataSources(List<DatabaseNode> dataSources) {
        this.dataSources = dataSources;
    }
}
