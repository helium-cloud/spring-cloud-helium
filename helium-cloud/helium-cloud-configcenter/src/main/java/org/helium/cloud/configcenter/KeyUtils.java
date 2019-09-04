package org.helium.cloud.configcenter;

import org.springframework.util.StringUtils;

public class KeyUtils {
    public static String getKey(String key, String group){
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(group)) {
            sb.append(group);
            sb.append(".");
        }
        sb.append(key);
        return sb.toString();
    }


}
