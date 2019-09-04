package com.feinno.urcs.data.redis.test.config;

import com.alibaba.fastjson.JSONObject;
import org.helium.database.ConnectionString;
import org.helium.redis.sentinel.RedisSentinelsCfg;

import java.io.IOException;

public class RedisConfigTest {
    public static void main(String[] args) throws IOException {
        String xxxx = "masterName=mymaster\n" +
                "database=0\n" +
                "maxWaitMillis=3000\n" +
                "maxTotal=100\n" +
                "minIdle=5\n" +
                "maxIdle=100\n" +
                "policy=Hash\n" +
                "nodeOrder=1\n" +
                "weight=10\n" +
                "enabled=1\n" +
                "addrs=10.10.220.149:17021;10.10.220.149:17022;10.10.220.149:17023";
        String parse = JSONObject.toJSONString(ConnectionString.fromText(xxxx).getProperties());
        System.out.println(parse);
        RedisSentinelsCfg cfg = JSONObject.parseObject(parse, RedisSentinelsCfg.class);
        System.out.println(cfg.getMasterName());
    }
}
