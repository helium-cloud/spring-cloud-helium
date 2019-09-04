package zconfig.enums;

import com.feinno.superpojo.type.EnumInteger;

/**
 * Created by liufeng on 2017/8/14.
 */
public enum LoadDataMode implements EnumInteger {
    INIT(0, "init"),
    UPDATE(1,"update");

    private int index;
    private String value;

    private LoadDataMode(int index, String value) {
        this.index = index;
        this.value = value;
    }

    public String getName() {
        return value;
    }

    @Override
    public int intValue() {
        return index;
    }
}
