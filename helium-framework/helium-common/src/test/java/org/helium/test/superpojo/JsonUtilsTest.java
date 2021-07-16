package org.helium.test.superpojo;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.helium.superpojo.JsonUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author wuhao
 * @createTime 2021-07-15 17:41:00
 */
public class JsonUtilsTest {
	@Test
	public void testToJson() throws JsonProcessingException {
		UserInfo userInfo = new UserInfo();
		userInfo.setTest("1111");
		String json = JsonUtils.toJson(userInfo);
		System.out.println(json);
		UserInfo userInfo1 = (UserInfo) JsonUtils.toObject(json, UserInfo.class);
		Assert.assertEquals(userInfo1.getTest(), userInfo.getTest());
	}
}
