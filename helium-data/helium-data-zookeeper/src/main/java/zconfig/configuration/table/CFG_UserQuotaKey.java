package zconfig.configuration.table;

import zconfig.configuration.args.ConfigTableField;
import zconfig.configuration.args.ConfigTableKey;

/**
 * Created by liufeng on 2016/1/20.
 */
public class CFG_UserQuotaKey extends ConfigTableKey {
    @ConfigTableField(value = "OwnerCarrier", isKeyField = true)
    private String ownerCarrier = "";

    @ConfigTableField(value = "OwnerStatus", isKeyField = true)
    private UserBasicStatus ownerStatus = UserBasicStatus.Normal;

    @ConfigTableField(value = "ExtendedService", isKeyField = true)
    private String extendedService = "";

    @ConfigTableField(value = "ContactCarrier", isKeyField = true)
    private String contactCarrier = "";

    @ConfigTableField(value = "QuotaType", isKeyField = true)
    private String quotaType = "";

    public CFG_UserQuotaKey() {
    }

    public CFG_UserQuotaKey(String ownerCarrier,
                            UserBasicStatus ownerStatus, String extendedService,
                            String contactCarrier, String quotaType) {
        this.ownerCarrier = ownerCarrier;
        this.ownerStatus = ownerStatus;
        this.extendedService = extendedService;
        this.contactCarrier = contactCarrier;
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

    public String getContactCarrier() {
        return contactCarrier;
    }

    public String getQuotaType() {
        return quotaType;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof CFG_UserQuotaKey))
            return false;
        CFG_UserQuotaKey target = (CFG_UserQuotaKey) obj;
        return ownerCarrier.equals(target.ownerCarrier)
                && ownerStatus.equals(target.ownerStatus)
                && extendedService.equals(target.extendedService)
                && contactCarrier.equals(target.extendedService)
                && quotaType.equals(target.quotaType);
    }

    public int hashCode() {
        return ownerCarrier.hashCode() ^ ownerStatus.hashCode()
                ^ extendedService.hashCode() ^ contactCarrier.hashCode() ^ quotaType.hashCode();
    }

    public String toString() {
        return String.format("CFG_UserQuotaFrequency: %s/%s:%s->%s",
                ownerCarrier, ownerStatus, extendedService, contactCarrier);
    }

}
