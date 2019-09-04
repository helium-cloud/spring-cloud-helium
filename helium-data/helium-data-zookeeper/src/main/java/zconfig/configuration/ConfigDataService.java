package zconfig.configuration;

import org.helium.framework.annotations.ServiceInterface;
import zconfig.configuration.enums.CarrierType;

/**
 * Created by liufeng on 2016/2/2.
 */
@ServiceInterface(id="urcs:ConfigDataService")
public interface ConfigDataService {
    CarrierType getCarrierType(String mobileNo);
}
