package org.helium.kafka.entity;

/**
 * 为提交数据重复消费策略
 * ATLEASTONCE:至少消费提交一次(新生产者,将失败数据放入消费队列)
 * ATMOSTONCE:最多消费一次(重启服务之前,不会再次消费)
 * Created by 2P on 19-1-18.
 */
public class Recurring {
    public static String  ATLEASTONCE="at_least_once";
    public static final String  ATMOSTONCE="at_most_once";
}

