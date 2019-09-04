package org.helium.framework.entitys.perfmon;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

/**
 * Created by Coral on 2015/8/17.
 */
public class ReportColumn extends SuperPojo {
    @Field(id = 1)
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
