package zconfig.configuration.enums;

import com.feinno.superpojo.type.EnumInteger;

/**
 * Created by liufeng on 2016/2/2.
 */
public enum CarrierType implements EnumInteger{
    UNKNOWN(0, "UNKNOWN"),    //未知
    CMCC(1, "CMCC"),           //中国移动
    CUCC(2, "CUCC"),           //中国联通
    CT(3, "CT"),               //中国电信
    ;
    private int index;
    private String value;

    private CarrierType(int index, String value) {
        this.index = index;
        this.value = value;
    }

    public String getCarrierName() {
        return value;
    }

    @Override
    public int intValue() {
        return index;
    }

    public static CarrierType convertTo(String value) {
        switch (value) {
            case "CMCC":
                return CMCC;
            case "CUCC":
                return CUCC;
            case "CT":
                return CT;
            default:
                return UNKNOWN;
        }
    }
}
