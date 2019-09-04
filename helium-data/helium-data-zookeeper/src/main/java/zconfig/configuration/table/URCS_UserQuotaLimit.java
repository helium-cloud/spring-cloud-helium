package zconfig.configuration.table;

import zconfig.configuration.args.ConfigTableField;
import zconfig.configuration.args.ConfigTableItem;

/**
 * Created by liufeng on 2016/1/18.
 */
public class URCS_UserQuotaLimit extends ConfigTableItem
{
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

    @ConfigTableField("QuotaLimit")
    private int quotaLimit;

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

    public int getQuotaLimit() {
        return quotaLimit;
    }
}
