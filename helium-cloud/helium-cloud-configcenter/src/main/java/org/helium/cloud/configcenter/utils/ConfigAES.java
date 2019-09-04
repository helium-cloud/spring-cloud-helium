package org.helium.cloud.configcenter.utils;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class ConfigAES {
	private static int length=128;
	/**
	 * 加密
	 * 
	 * @param content
	 *            需要加密的内容
	 * @param password
	 *            加密密码
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte [] content, byte[] password)
			throws Exception {
		SecretKeySpec key = new SecretKeySpec(password, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 创建密码器
		byte[] byteContent = content;
		cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
		byte[] result = cipher.doFinal(byteContent);
		return result; // 加密

	}
	
	public static byte[] encrypt(String content, String password)
			throws Exception {
		return encrypt(content.getBytes("UTF-8"), password.getBytes("UTF-8"));
	}

	public static byte[] encrypt(byte[] content, String password)
			throws Exception {
		return encrypt(content, password.getBytes("UTF-8"));

	}

	/**
	 * 解密
	 * 
	 * @param content
	 *            待解密内容
	 * @param password
	 *            解密密钥
	 * @return
	 */
	public static byte[] decrypt(byte[] content, String password)
			throws Exception {

		
		return decrypt(content,password.getBytes("UTF-8")); // 加密
	}
	
	public static byte[] decrypt(byte[] content, byte[] password)
			throws Exception {
		SecretKeySpec key = new SecretKeySpec(password, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 创建密码器
		cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
		byte[] result = cipher.doFinal(content);
		return result; // 解密
	}

	public static String encrypt2Str(String content, String password) throws Exception {
		byte[] encryptResult = encrypt(content, password);
		return ConfigBase64.encode(encryptResult);
	}

	public static String decrypt2Str(String content, String password) throws Exception {

		byte[] decryptResult = decrypt(ConfigBase64.decode(content), password);
		return new String(decryptResult,"UTF-8");
	}

	public static void main(String[] args) throws Exception {
		String content = "testenc";
		String pwd = "77889900";

		String encContent = ConfigAES.encrypt2Str(content, pwd);
		System.out.println("encContent:" + encContent);
		String decContent = ConfigAES.decrypt2Str(content, pwd);
		System.out.println("decContent:" + decContent);

	}
}
