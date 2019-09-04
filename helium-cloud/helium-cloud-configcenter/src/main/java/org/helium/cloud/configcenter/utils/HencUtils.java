package org.helium.cloud.configcenter.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HencUtils {
    private static final String pattern = "HENC\\(.*\\)";;
    public static String getDecoder(String source){
        // 1.创建 Pattern 对象
        Pattern compile = Pattern.compile(pattern);

        // 2.现在创建 matcher 对象
        Matcher matcher = compile.matcher(source);

        // 3.循环解密
        String dest = source;
        while (matcher.find()){
            String enc = matcher.group();
            String tmp =  enc.substring(5, enc.length() - 1);
            //TODO 解密
            String dnc = "de" +  tmp;
            dest = matcher.replaceFirst(dnc);
            matcher = compile.matcher(dest);

        }
        return dest;
    }
}
