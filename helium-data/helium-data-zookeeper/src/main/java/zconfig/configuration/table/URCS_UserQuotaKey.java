package zconfig.configuration.table;

import zconfig.configuration.args.ConfigTableField;
import zconfig.configuration.args.ConfigTableKey;

/**
 * Created by liufeng on 2016/1/18.
 */
public class URCS_UserQuotaKey extends ConfigTableKey {
    @ConfigTableField(value = "OwnerCarrier", isKeyField = true)
    private String ownerCarrier = "";

    @ConfigTableField(value = "OwnerStatus", isKeyField = true)
    private UserBasicStatus ownerStatus = UserBasicStatus.Normal;

    @ConfigTableField(value = "ExtendedService", isKeyField = true)
    private String extendedService = "";

    @ConfigTableField(value = "BizService", isKeyField = true)
    private String bizService = "";

    @ConfigTableField(value = "QuotaType", isKeyField = true)
    private String quotaType = "";

    public URCS_UserQuotaKey() {
    }

    public URCS_UserQuotaKey(String ownerCarrier, UserBasicStatus ownerStatus, String extendedService, String bizService, String quotaType) {
        this.ownerCarrier = ownerCarrier;
        this.ownerStatus = ownerStatus;
        this.extendedService = extendedService;
        this.bizService = bizService;
        this.quotaType = quotaType;
    }

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

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof URCS_UserQuotaKey))
            return false;
        URCS_UserQuotaKey target = (URCS_UserQuotaKey) obj;
        return ownerCarrier.equals(target.ownerCarrier)
                && ownerStatus.equals(target.ownerStatus)
                && extendedService.equals(target.extendedService)
                && bizService.equals(target.bizService)
                && quotaType.equals(target.quotaType);
    }

    public int hashCode() {
        return ownerCarrier.hashCode()
                ^ ownerStatus.hashCode()
                ^ extendedService.hashCode()
                ^ bizService.hashCode()
                ^ quotaType.hashCode();
    }

    public String toString() {
        return String.format("CFG_UserQuotaFrequency: %s:%s:%s:%s:%s",
                ownerCarrier, ownerStatus, extendedService, bizService, quotaType);
    }
}
