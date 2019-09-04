package zconfig.configuration.table;

import zconfig.configuration.args.ConfigTableField;
import zconfig.configuration.args.ConfigTableItem;

/**
 * Created by liufeng on 2016/1/20.
 */
public class CFG_UserQuotaLimit extends ConfigTableItem
{
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

    @ConfigTableField("QuotaLimit")
    private int quotaLimit;

    public String getOwnerCarrier()
    {
        return ownerCarrier;
    }

    public UserBasicStatus getOwnerStatus()
    {
        return ownerStatus;
    }

    public String getExtendedService()
    {
        return extendedService;
    }

    public String getContactCarrier()
    {
        return contactCarrier;
    }

    public int getQuotaLimit()
    {
        return quotaLimit;
    }

    public String getQuotaType() {
        return quotaType;
    }
}
