package org.helium.cloud.config;

import org.helium.cloud.configcenter.utils.HencUtils;
import org.junit.Test;

public class HencTest {
    static String enc = "DriverClass=com.mysql.jdbc.Driver\n" +
            "JdbcUrl=jdbc:mysql://10.10.220.91:3307/cinf_db_new?autoReconnect=true\n" +
            "user=HENC(pgm)\n" +
            "password=HENC(pgmfetion)\n" +
            "maximumPoolSize=10\n" +
            "minimumIdle=2\n" +
            "connectionTimeout=10000\n" +
            "validationTimeout=2000\n" +
            "maxLifetime=1800000\n" +
            "idleTimeout=600000\n" +
            "connectionTestQuery=select 1";
    @Test
    public void testEnc(){
        long time = System.currentTimeMillis();
        System.out.println(HencUtils.getDecoder(enc));
        System.out.println("耗时：" + (System.currentTimeMillis() - time));
    }
}
