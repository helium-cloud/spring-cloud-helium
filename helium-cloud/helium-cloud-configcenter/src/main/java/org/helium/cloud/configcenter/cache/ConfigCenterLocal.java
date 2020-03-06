/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.helium.cloud.configcenter.cache;


import org.helium.cloud.configcenter.ConfigCenterClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地缓存文件配置
 */
public class ConfigCenterLocal {


    private Properties properties = new Properties();

    private ConcurrentHashMap configMap = new ConcurrentHashMap();

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigCenterClient.class);

    public String getConfig(String key) {
        Object value = configMap.get(key);
        if (!StringUtils.isEmpty(value)) {
            return (String) value;
        }
        return null;
    }

    public void putConfig(String key, String content) {
        configMap.put(key, content);
    }

    public void deleteConfig(String key) {
        configMap.remove(key);
    }


    private void saveConfig(String file) {
        try {
//            Properties prop = new Properties();
//            prop.putAll(configMap);
//            FileOutputStream outputStream = new FileOutputStream(file);
//            prop.store(outputStream, "cache cloud");
//            outputStream.close();
        } catch (Exception e) {
            LOGGER.error("saveConfig:{}", file, e);
        }
    }

    public void loadConfig(String file) {
        try {
            Properties prop = new Properties();
            File fileLoad = new File(file);
            LOGGER.info("load cur path file:{}", fileLoad.getAbsolutePath());
            if (!fileLoad.exists()) {
                fileLoad = new File(ResourceUtils.getURL("classpath:").getPath() + file);
                LOGGER.info("load classpath file:{}", fileLoad.getAbsolutePath());
            }
            if (!fileLoad.exists()){
                return;
            }
            FileInputStream inputStream = new FileInputStream(fileLoad);
            //将流中的数据加载进集合
            prop.load(inputStream);
            synchronized (configMap) {
                configMap.putAll(prop);
            }
            inputStream.close();

        } catch (Exception e) {
            LOGGER.error("loadConfig Exception:{}", file, e);
        }

    }

    /**
     * 定时器缓存配置文件
     */
    public void timerCheck(String file) {
        TimerTask task = new TimerTask() {
            public void run() {
                saveConfig(file);
            }
        };
        Timer timer = new Timer();
        long delay = 120 * 1000;
        long intevalPeriod = 120 * 1000;
        timer.scheduleAtFixedRate(task, delay, intevalPeriod);
    }

}
