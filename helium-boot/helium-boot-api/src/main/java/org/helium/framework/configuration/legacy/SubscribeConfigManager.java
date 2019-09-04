package org.helium.framework.configuration.legacy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liufeng on 2016/1/8.
 */
public class SubscribeConfigManager {
    private static final Map<String, String> mapSubscribeConfig = new ConcurrentHashMap<>();

    public static void putSubscribeConfig(String path, ConfigType configType) {

        String key = String.format("%s:%s", path, configType.name());

        if (!mapSubscribeConfig.containsKey(key))
        {
            mapSubscribeConfig.put(key, "1");
        }
    }

    public static boolean isSubscribeConfig(String path, ConfigType configType) {
        String key = String.format("%s:%s", path, configType.name());

        if (mapSubscribeConfig.containsKey(key))
        {
            return true;
        }
        else {
            return false;
        }
    }
}
