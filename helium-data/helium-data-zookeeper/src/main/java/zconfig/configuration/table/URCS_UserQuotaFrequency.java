package zconfig.configuration.table;

import zconfig.configuration.args.ConfigTableField;
import zconfig.configuration.args.ConfigTableItem;

/**
 * Created by liufeng on 2016/1/18.
 */
public class URCS_UserQuotaFrequency extends ConfigTableItem {

    @ConfigTableField("OwnerCarrier")
    private String ownerCarrier = "";

    @ConfigTableField("OwnerStatus")
    private UserBasicStatus ownerStatus = UserBasicStatus.Any;

    @ConfigTableField("ExtendedService")
    private String extendedService = "";

    @ConfigTableField("BizService")
    private String bizService = "";

    @ConfigTableField("QuotaType")
    private String quotaType = "";

    @ConfigTableField("QuotaMinuteLimit")
    private int quotaMinuteLimit;

    @ConfigTableField("QuotaHourLimit")
    private int quotaHourLimit;

    @ConfigTableField("QuotaDayLimit")
    private int quotaDayLimit;

    @ConfigTableField("QuotaMonthLimit")
    private int quotaMonthLimit = 1;

    public String getOwnerCarrier() {
        return ownerCarrier;
    }

    public UserBasicStatus getOwnerStatus() {
        return ownerStatus;
    }

    public String getExtendedService() {
        return extendedService;
    }

    public String getBizService() {
        return bizService;
    }

    public String getQuotaType() {
        return quotaType;
    }

    public int getQuotaMinuteLimit() {
        return quotaMinuteLimit;
    }

    public int getQuotaHourLimit() {
        return quotaHourLimit;
    }

    public int getQuotaDayLimit() {
        return quotaDayLimit;
    }

    public int getQuotaMonthLimit() {
        return quotaMonthLimit;
    }
}
