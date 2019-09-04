package org.helium.redis.sentinel;

import org.helium.util.StringUtils;

/**
 * @author Li.Hongbo <lihongbo@feinno.com>
 * @date 2015年2月10日 下午5:59:26
 */
public class RedisKey {
    // private long numKey;
    private String strKey;

    public RedisKey(long key) {
        this.strKey = String.valueOf(key);
    }

    public RedisKey(int key) {
        this.strKey = String.valueOf(key);
    }

    public RedisKey(String key) {
        if (StringUtils.isNullOrEmpty(key))
            throw new IllegalArgumentException("rediskey can't be null or empty");
        this.strKey = key;
    }

    public String getStrKey() {
        return strKey;
    }

    public int getHashValueForRoute() {
        return compatibleGetHashCode(getHashTag());
    }

    private String getHashTag() {
        String hashTag = strKey;
        int pos = strKey.indexOf("{");
        if (pos >= 0) {
            int pos2 = strKey.indexOf("}");
            if ((pos2 >= 0) && ((pos2 - pos) >= 2)) {
                hashTag = strKey.substring(pos + 1, pos2);
            }
        }
        return hashTag;
    }

    private static int compatibleGetHashCode(String s) {
        int num = 0x15051505;
        int num2 = num;

        int np;
        int j = 0;
        int length = s.length();
        for (int i = length; i > 0; i -= 4) {
            char[] ss = s.toCharArray();
            np = (length <= j) ? 0 : ss[j] | ((length > j + 1 ? ss[j + 1] : 0) << 16);
            num = (((num << 5) + num) + (num >> 0x1b)) ^ np;
            if (i <= 2) {
                break;
            }
            j += 2;
            np = (length <= j) ? 0 : ss[j] | ((length > j + 1 ? ss[j + 1] : 0) << 16);
            num2 = (((num2 << 5) + num2) + (num2 >> 0x1b)) ^ np;
            j += 2;
        }
        return (num + (num2 * 0x5d588b65));
    }

    public String toLogString() {
        return String.format("RedisKey :strKey=%s ", this.strKey);
    }


    public static void main(String[] args) {
        RedisKey key = new RedisKey("{1356}.abc");
        System.out.print(key.getHashTag());
    }
}
