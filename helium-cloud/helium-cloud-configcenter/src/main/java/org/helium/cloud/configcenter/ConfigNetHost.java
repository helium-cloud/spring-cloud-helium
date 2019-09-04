package org.helium.cloud.configcenter;

import com.feinno.superpojo.util.FileUtil;
import com.feinno.superpojo.util.StringUtils;

import java.io.File;

/**
 * ConfigCenterClient 自定义客户端
 * 需要能访问 etc/hosts权限
 * 是否需要添加版本号
 */
public class ConfigNetHost {
    public String sign = "#TCNH_FORJAVA_REFER";
    private ConfigCenterProperties configCenterProperties;

    public ConfigNetHost() {
    }

    public ConfigNetHost(ConfigCenterProperties configCenterProperties) {
        this.configCenterProperties = configCenterProperties;
    }

    public void refreshFile() {
        if (configCenterProperties != null && !StringUtils.isNullOrEmpty(configCenterProperties.getHosts())) {
            String[] hostArray = configCenterProperties.getHosts().split(",");
            if (hostArray.length > 0){
                refreshFile("/etc/hosts", hostArray, false);
            }

        }
    }

    public void refreshFile(String refreshFile, String[] stringList, boolean forceRefresh) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        for (String item : stringList) {
            stringBuilder.append(item);
            stringBuilder.append("\n");
        }
        refreshFile(refreshFile, stringBuilder.toString(), forceRefresh);
    }

    public void refreshFile(String refreshFile, String addContent, boolean forceRefresh) {
        File file = new File(refreshFile);
        if (file.exists()) {
            String content = FileUtil.read(file);
            if (content.contains(sign) && !forceRefresh) {
                return;
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(content);
                stringBuilder.append("\n");
                stringBuilder.append(sign);
                stringBuilder.append("\n");
                stringBuilder.append(addContent);
                stringBuilder.append("\n");
                FileUtil.write(stringBuilder.toString(), refreshFile);
            }
        }

    }


}
