package org.helium.sample.boot.net;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Test{
    public static void main(String[] args) {
        System.out.println(getCPUSerial());
    }
    /**获取本机CPU信息
     */
    private static String getCPUSerial() {
        String result = "";
        try {
            Process process = Runtime.getRuntime().exec("sudo dmidecode -s system-uuid");
            InputStream in;
            BufferedReader br;
            in = process.getInputStream();
            br = new BufferedReader(new InputStreamReader(in));
            while (in.read() != -1) {
                result = br.readLine();
            }
            br.close();
            in.close();
            process.destroy();
        } catch (Throwable e) {
            result = "0000000-0000-0000-0000-000000000000";
        }
        return result;
    }
}