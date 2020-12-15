package org.helium.framework.spring.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class AESUtil {
	private static final String IV_STRING = "sdf4ddfsFD86Vdf2";
	private static final String encoding = "UTF-8";

	public static String encryptAES(String content, String key) throws Exception {
		byte[] byteContent = content.getBytes(encoding);
		// 注意，为了能与 iOS 统一
		// 这里的 key 不可以使用 KeyGenerator、SecureRandom、SecretKey 生成
		byte[] enCodeFormat = key.getBytes(encoding);
		SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
		byte[] initParam = IV_STRING.getBytes(encoding);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
		// 指定加密的算法、工作模式和填充方式
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
		byte[] encryptedBytes = cipher.doFinal(byteContent);
		// 同样对加密后数据进行 base64 编码
		String base64 = Base64Utils.encodeToUrlSafeString(encryptedBytes);
		//进行url编码 去掉= ? &
		return base64;
	}

	public static String decryptAES(String content, String key) throws Exception {
		// base64 解码
		byte[] encryptedBytes = Base64Utils.decodeFromUrlSafeString(content);
		byte[] enCodeFormat = key.getBytes(encoding);
		SecretKeySpec secretKey = new SecretKeySpec(enCodeFormat, "AES");
		byte[] initParam = IV_STRING.getBytes(encoding);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
		byte[] result = cipher.doFinal(encryptedBytes);
		return new String(result, encoding);
	}

	public static void main(String[] args) throws Exception {
		JSONObject json = new JSONObject();
		json.put("custNum", "111111");
		String content = json.toJSONString();
		System.out.println("加密前：" + content);
		String key = "djadiKJdj49dFJLd";
		System.out.println("加密密钥和解密密钥：" + key);
		String encrypt = encryptAES(content, key);
		System.out.println("加密后：" + encrypt);
		String decrypt = decryptAES(encrypt, key);
		System.out.println("解密后：" + decrypt);
	}
}