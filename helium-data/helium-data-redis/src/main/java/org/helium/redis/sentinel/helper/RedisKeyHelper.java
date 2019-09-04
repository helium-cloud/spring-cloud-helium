package org.helium.redis.sentinel.helper;

import redis.clients.util.SafeEncoder;

public class RedisKeyHelper {
    public static final String WRITE_LOCK = "w";
    private static final String PUBLOCKKEY = "NullLockKey";
    public static final byte[] WRITE_LOCK_B = SafeEncoder.encode(WRITE_LOCK);

    public static final String READ_LOCK = "r";
    public static final byte[] READ_LOCK_B = SafeEncoder.encode(READ_LOCK);
    private static final String LOCK_SUFFIX_S = ".lock";
    private static final byte[] LOCK_SUFFIX_B = LOCK_SUFFIX_S.getBytes();

    public static String getLockKey(String s) {
        return (null == s || s.trim().equals("")) ? PUBLOCKKEY + LOCK_SUFFIX_S : s + LOCK_SUFFIX_S;
    }

    public static void main(String[] args) {
        System.out.println(getLockKey(null));
        System.out.println(getLockKey("    "));
        System.out.println(getLockKey("123abc"));
    }
}
