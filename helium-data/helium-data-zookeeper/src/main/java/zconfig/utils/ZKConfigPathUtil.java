package zconfig.utils;


import zconfig.ConstantKey;
import zconfig.args.ConfigTableArgs;
import zconfig.args.ConfigTextArgs;

/**
 * 提供一个统一的地方获取路径
 * Created by lyfx on 17-10-27.
 */
public class ZKConfigPathUtil {

    /**
     * 获得zk child节点 Text  的path
     *
     * @param path
     * @return
     */
    public static String getConfigChildTextPath(String path) {
        return ConstantKey.ZK_CONFIG_CHILD_NOTE_TEXT_PATH + "/" + path;
    }


    /**
     * 获得zk child节点 Text  的path
     *
     * @param path
     * @return
     */
    public static String getConfigChildTablePath(String path) {
        return ConstantKey.ZK_CONFIG_CHILD_NOTE_TABLE_PATH + "/" + path;
    }


    public static String getConfigChildPath(String path, Class<?> clazz) {
        if (clazz.equals(ConfigTableArgs.class)) {
            return getConfigChildTablePath(path);
        }else if (clazz.equals(ConfigTextArgs.class)) {
            return getConfigChildTextPath(path);
        }
        return null;
    }
}
