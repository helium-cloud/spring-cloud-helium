package zconfig.configuration;


import org.helium.framework.annotations.FieldSetter;
import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.configuration.Environments;
import org.helium.framework.tag.Initializer;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zconfig.configuration.args.ConfigTable;
import zconfig.configuration.args.ConfigUpdateAction;
import zconfig.configuration.enums.CarrierType;
import zconfig.configuration.helper.ConfigDataHelper;
import zconfig.configuration.table.CFG_CarrierMapping;
import zconfig.loaders.ConfigurationLoader;

/**
 * Created by liufeng on 2016/2/2.
 */
@ServiceImplementation
public class ConfigDataServiceImpl implements ConfigDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigDataServiceImpl.class);

    @FieldSetter(value = "CFG_CarrierMapping", loader = ConfigurationLoader.class)
    private ConfigTable<Long, CFG_CarrierMapping> carrierMappings;

    private String centerUrl;

    @Initializer
    public void initial() {
        centerUrl = Environments.getVar(HAConfigurator.CENTER_URL_KEY);

        if (!StringUtils.isNullOrEmpty(centerUrl)) {
            try {
                carrierMappings.addEvent(new ConfigUpdateAction<ConfigTable<Long, CFG_CarrierMapping>>() {
                    @Override
                    public void run(ConfigTable<Long, CFG_CarrierMapping> table) {
                        carrierMappings = table;
                    }
                });

            } catch (Exception ex) {
                LOGGER.error(String.format("ConfigDataServiceImpl initial error, %s", ex.getMessage()), ex);
            }
        }
    }

    @Override
    public CarrierType getCarrierType(String mobileNo) {
        if (carrierMappings == null)
        {
            return CarrierType.UNKNOWN;
        }

        String carrierName = ConfigDataHelper.getCarrier(carrierMappings, Long.parseLong(mobileNo));

        return CarrierType.convertTo(carrierName);
    }
}
