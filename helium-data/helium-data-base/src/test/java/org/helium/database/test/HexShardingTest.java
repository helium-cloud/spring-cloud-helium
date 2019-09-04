package org.helium.database.test;

import com.feinno.superpojo.util.StringUtils;

import java.util.UUID;

/**
 * Created by Coral on 8/15/16.
 */
public class HexShardingTest {
	public static void main(String[] args) {
		String uuid = UUID.randomUUID().toString();
		String s = uuid.substring(uuid.length() - 2);
		byte[] b = StringUtils.fromHexString(s);
		System.out.println("index:" + b[0]);

		System.out.println(String.format("%02d", 1));
		System.out.println(String.format("%02x", 11));

	}
}
