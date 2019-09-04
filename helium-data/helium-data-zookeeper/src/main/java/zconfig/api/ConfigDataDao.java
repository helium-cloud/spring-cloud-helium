package zconfig.api;

import org.helium.framework.annotations.ServiceInterface;
import zconfig.args.ConfigTableArgs;
import zconfig.args.ConfigTextArgs;
import zconfig.args.ConfigUpdateNotifyArgs;

import java.util.List;

/**
 * Created by liufeng on 2017/8/14.
 */
@ServiceInterface(id="config:ConfigDataDao")
public interface ConfigDataDao {
    List<ConfigTableArgs> loadConfigTable();

    List<ConfigTextArgs> loadConfigText();

    void insertConfigUpdateNotify(ConfigUpdateNotifyArgs configUpdateNotifyArgs);
    void updateConfigTableArgs(ConfigTableArgs configTableArgs) ;
    void updateConfigTextArgs(ConfigTextArgs configTextArgs);
    ConfigTextArgs selectConfigTextArgs(String configKey);
    ConfigTableArgs selectConfigTableArgs(String tableName) ;
}
