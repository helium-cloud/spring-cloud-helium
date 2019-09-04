package zconfig.configuration.table;

import zconfig.configuration.args.ConfigTableField;
import zconfig.configuration.args.ConfigTableItem;

/**
 * Created by liufeng on 2016/1/20.
 */
public class CFG_UserQuotaFrequency extends ConfigTableItem {

    @ConfigTableField("OwnerCarrier")
    private String ownerCarrier = "";

    @ConfigTableField("OwnerStatus")
    private UserBasicStatus ownerStatus = UserBasicStatus.Any;

    @ConfigTableField("ExtendedService")
    private String extendedService = "";

    @ConfigTableField("ContactCarrier")
    private String contactCarrier = "";

    @ConfigTableField("QuotaType")
    private String quotaType = "";

    // 总配额数量
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

    public String getContactCarrier() {
        return contactCarrier;
    }

    public String getQuotaType() {
        return quotaType;
    }

    public int getQuotaDayLimit() {
        return quotaDayLimit;
    }

    public int getQuotaMonthLimit() {
        return quotaMonthLimit;
    }
}