package org.helium.superpojo.utils;

public class HexUtil {

	public static String toHexString(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			sb.append(toHexString(b[i]).toUpperCase());
			sb.append(" ");
		}
		sb = sb.length() > 0 ? sb.delete(sb.length() - 1, sb.length()) : sb;
		return sb.toString();
	}

	public static String toHexString(byte b) {
		String hex = Integer.toHexString(b & 0xFF).toUpperCase();
		if (hex.length() == 1) {
			hex = '0' + hex;
		}
		return hex;
	}
}
