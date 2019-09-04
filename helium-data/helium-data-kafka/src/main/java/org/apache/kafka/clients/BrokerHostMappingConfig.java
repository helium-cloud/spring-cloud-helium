package org.apache.kafka.clients;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * config line example
 * original host and port   replaced host and port
 * 10.10.220.123:9092 10.10.220.124:9093
 */
public class BrokerHostMappingConfig {

    public static BrokerHostMappingConfig Instance = new BrokerHostMappingConfig();

    private boolean hasInitialized = false;

    private ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

    private BrokerHostMappingConfig() {

    }

    public synchronized void initConfig(File configFile) throws IOException {
        if (!hasInitialized) {
            if (!configFile.exists()) {
                throw new IOException("not find file");
            }

            FileReader reader = null;
            BufferedReader br = null;
            try {
                reader = new FileReader(configFile);
                br = new BufferedReader(reader);

                String str = null;
                while ((str = br.readLine()) != null) {
                    String[] parts = str.split("/");
                    map.put(parts[0], parts[1]);
                }

            } finally {

                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            hasInitialized = true;
        }
    }

    public ConcurrentHashMap<String, String> getMap() {
        return map;
    }
}
