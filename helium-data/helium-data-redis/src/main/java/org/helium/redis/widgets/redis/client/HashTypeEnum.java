package org.helium.redis.widgets.redis.client;

import com.feinno.superpojo.type.EnumInteger;

/**
 * Created by yibo on 2017-2-13.
 */


public enum HashTypeEnum implements EnumInteger {


    String(1),

    Long(2),;


    int value;

    private HashTypeEnum(int value) {
        this.value = value;
    }

    public int intValue() {
        return value;
    }

}

