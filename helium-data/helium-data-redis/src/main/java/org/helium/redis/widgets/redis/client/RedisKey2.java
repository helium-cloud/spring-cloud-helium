package org.helium.redis.widgets.redis.client;

/**
 * Created by yibo on 2017-2-10.
 */


public class RedisKey2 {

    private String strKey;
    private long longKey;
    private HashTypeEnum hashTypeEnum = HashTypeEnum.String;

    public RedisKey2(long key) {
        this.strKey = String.valueOf(key);
        this.longKey = key;
        this.hashTypeEnum = HashTypeEnum.Long;
    }

    public RedisKey2(int key) {
        this.strKey = String.valueOf(key);
        this.longKey = key;
        this.hashTypeEnum = HashTypeEnum.Long;
    }

    public RedisKey2(String key) {
//        if (StringUtils.isNullOrEmpty(key)) {
//
//            throw new IllegalArgumentException("redis key can't be null or empty");
//        }

        if(key==null)
        {
            key="";
        }

        this.strKey = key;
        this.hashTypeEnum = HashTypeEnum.String;
    }

    public String getStrKey() {
        return strKey;
    }

    public int getHashValueForRoute() {

        if (this.hashTypeEnum == HashTypeEnum.Long) {
            return compatibleGetHashCode(longKey);
        } else if (this.hashTypeEnum == HashTypeEnum.String) {
            return compatibleGetHashCode(this.strKey);
        } else {
            return 0;
        }

    }

    public static int compatibleGetHashCode(long n) {
        return ((int) n) ^ ((int) (n >> 32));
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

    @Override
    public String toString() {
        return String.format("RedisKey :strKey=%s ", this.strKey);
    }


}
