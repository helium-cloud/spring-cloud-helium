package org.helium.test.superpojo;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.helium.superpojo.ProtostuffUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author wuhao
 * @createTime 2021-07-15 17:45:00
 */
public class ProtostuffUtilsTest {
	@Test
	public void testProtostuff() throws JsonProcessingException {
		UserInfo userInfo = new UserInfo();
		userInfo.setTest("1111");
		byte[] bytes = ProtostuffUtils.toBytes(userInfo);
		UserInfo userInfo1 = (UserInfo) ProtostuffUtils.toObject(bytes, UserInfo.class);
		Assert.assertEquals(userInfo1.getTest(), userInfo.getTest());
	}
}
