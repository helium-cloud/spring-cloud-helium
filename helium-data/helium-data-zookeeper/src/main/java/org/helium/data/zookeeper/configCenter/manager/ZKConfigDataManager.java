package org.helium.data.zookeeper.configCenter.manager;

import org.helium.data.zookeeper.configCenter.entity.ZKConfigTable;
import zconfig.configuration.args.ConfigType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liufeng on 2017/8/10.
 */
public class ZKConfigDataManager {

    private static final Map<String, ZKConfigTable> mapConfigTable = new ConcurrentHashMap<>();
    private static final Map<String, String> mapConfigText = new ConcurrentHashMap<>();

    public static void putConfigTable(String path, ConfigType configType, ZKConfigTable data) {

        String key = String.format("%s:%s", path, configType.name());

        if (!mapConfigTable.containsKey(key)) {
            mapConfigTable.put(key, data);
        } else {
            mapConfigTable.remove(key);
            mapConfigTable.put(key, data);
        }
    }

    public static ZKConfigTable getConfigTable(String path, ConfigType configType) {

        String key = String.format("%s:%s", path, configType.name());

        if (mapConfigTable.containsKey(key)) {
            return mapConfigTable.get(key);
        } else {
            return null;
        }
    }

    public static void putConfigText(String path, ConfigType configType, String text) {

        String key = String.format("%s:%s", path, configType.name());

        if (!mapConfigText.containsKey(key)) {
            mapConfigText.put(key, text);
        } else {
            mapConfigText.remove(key);
            mapConfigText.put(key, text);
        }
    }

    public static String getConfigText(String path, ConfigType configType) {

        String key = String.format("%s:%s", path, configType.name());

        if (mapConfigText.containsKey(key)) {
            return mapConfigText.get(key);
        } else {
            return null;
        }
    }
}
