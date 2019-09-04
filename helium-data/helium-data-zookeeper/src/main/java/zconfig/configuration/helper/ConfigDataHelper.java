package zconfig.configuration.helper;

import zconfig.configuration.args.ConfigTable;
import zconfig.configuration.table.CFG_CarrierMapping;

/**
 * Created by liufeng on 2016/2/2.
 */
public class ConfigDataHelper {
    public static String getCarrier(ConfigTable<Long, CFG_CarrierMapping> carrierMappings, long mobileNo){
        for (CFG_CarrierMapping mapping : carrierMappings.getValues()) {
            if (mobileNo >= mapping.getMappingStart() && mobileNo <= mapping.getMappingEnd()) {
                return mapping.getCarrierName();
            }
        }

        return null;
    }
}
