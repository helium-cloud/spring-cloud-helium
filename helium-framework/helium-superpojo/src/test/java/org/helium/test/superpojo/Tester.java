package org.helium.test.superpojo;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.SuperPojoManager;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;
import com.feinno.superpojo.type.Guid;
import com.feinno.superpojo.util.FileUtil;
import com.feinno.superpojo.util.HexUtil;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.helium.test.superpojo.bean.DataCreater;
import org.helium.test.superpojo.bean.FullElementsBean;
import org.helium.test.superpojo.bean.ParentBean;

import java.util.*;

public class Tester {

	public static void main(String args[]) throws Exception {
		// new Tester().testWrite();
		// new Tester().testParse();
		// new Tester().testFullElements();
		// new Tester().testGUID();
		// new Tester().parse();
		// new Tester().testChildBean();
		FullElementsBean bean = DataCreater.newFullElementsBean(true);
		
		System.out.println(new String(bean.toPbByteArray()));
		System.out.println(bean.toJsonObject());
		System.out.println(new String(bean.toXmlByteArray()));
		String xml = new String(bean.toXmlByteArray());
		FullElementsBean bean2 = new FullElementsBean();
		bean2.parseXmlFrom(xml);
		System.out.println(bean2.toJsonObject());
	}

	public void testChildBean() {
		ParentBean.ChildBean bean = DataCreater.newChildrenBean(true);
		System.out.println(Arrays.toString(bean.toPbByteArray()));
		System.out.println(bean);
		System.out.println(new String(bean.toXmlByteArray()));

		ParentBean.ChildBean result = new ParentBean.ChildBean();
		result.parseXmlFrom(new String(bean.toXmlByteArray()));
		System.out.println(new String(result.toXmlByteArray()));
		result = SuperPojoManager.parseJsonFrom(result.toString(), ParentBean.ChildBean.class);
		System.out.println(new String(result.toXmlByteArray()));
	}

	public void parse() {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><FullElementsBean int_Test=\"\" long_Test=\"100\"><float>1000.1</float><double>10000.1</double><boolean_Test>true</boolean_Test><short_Test>126</short_Test><byte_Test></byte_Test><Integer_obj>20</Integer_obj></FullElementsBean>";
		System.out.println(xml);
		FullElementsBean bean = new FullElementsBean();
		bean.parseXmlFrom(xml);
		System.out.println(bean);
		// System.out.println(new String(bean.toXmlByteArray()));
	}

	public void testGUID() {
		Bean bean = new Bean();
		bean.setGuid(Guid.randomGuid());
		String xml = new String(bean.toXmlByteArray());
		System.out.println(xml);

		Bean result = new Bean();
		result.parseXmlFrom(xml);
		xml = new String(result.toXmlByteArray());
		System.out.println(xml);

		System.out.println(bean.toJsonObject());
		System.out.println(result.toJsonObject());
		System.out.println(bean.toJsonObject().equals(result.toJsonObject()));
		System.out.println(bean.toJsonObject().toString().equals(result.toJsonObject().toString()));
	}

	public void testFullElements() throws Exception {
		FullElementsBean bean = DataCreater.newFullElementsBean(true);

		FullElementsBean xmlResult = DataCreater.newFullElementsBean(false);
		xmlResult.parseXmlFrom(new String(bean.toXmlByteArray()));
		System.out.println("xml parse result : " + bean.toJsonObject().equals(xmlResult.toJsonObject()));

		FullElementsBean jsonResult = DataCreater.newFullElementsBean(false);
		// jsonResult.parseJsonFrom(bean.toJsonObject().toString());
		jsonResult = SuperPojoManager.parseJsonFrom(bean.toJsonObject().toString(), FullElementsBean.class);
		System.out.println("json parse result : " + bean.toJsonObject().equals(jsonResult.toJsonObject()));
		FileUtil.write(bean.toString(), "/data/tmp/json1.txt");
		FileUtil.write(jsonResult.toString(), "/data/tmp/json2.txt");

	}

	public void testParse() {
		UserInfo user = new UserInfo();
		user.setId(1);
		user.setName("Feinno");
		user.setSex(UserInfo.SexEnum.FEMALE);
		user.setBirthday(new Date());
		user.setNameSpace("http://www.w3school.com.cn/xml/");
		user.setAddress("北京市朝阳区北苑路甲13号院北辰泰岳大厦18层");
		user.setSqlDate(new java.sql.Date(System.currentTimeMillis()));
		UserInfo user2 = new UserInfo();
		user2.parseXmlFrom(new String(user.toXmlByteArray()));
		List<UserInfo> list = new ArrayList<UserInfo>();
		list.add(user2);
		list.add(user2);
		user.setFriends(list);
		user.setBestFriend(user2);
		Map<String, UserInfo> map = new HashMap<String, UserInfo>();
		user.setFriendMap(map);
		map.put("T1", user2);
		map.put("T2", user2);
		map.put("T3", user2);
		Map<String, String> strMap = new HashMap<String, String>();
		strMap.put("1", "A");
		strMap.put("2", "B");
		strMap.put("3", "C");
		user.setStringMap(strMap);
		// 反序列化
		UserInfo result1 = new UserInfo();
		result1.parseXmlFrom(new String(user.toXmlByteArray()));
		// 使用上一步骤反序列化的结果，再反序列化
		UserInfo result2 = new UserInfo();
		result2.parseXmlFrom(new String(result1.toXmlByteArray()));
		// 比对验证
		Assert.assertEquals(result1.toJsonObject(), result2.toJsonObject());

		System.out.println(new String(user.toXmlByteArray()));
		System.out.println(new String(result1.toXmlByteArray()));

		byte[] pbBuffer = result1.toPbByteArray();
		byte[] xmlBuffer = result1.toXmlByteArray();
		JsonObject jsonObject = result1.toJsonObject();

		System.out.println(HexUtil.toHexString(pbBuffer));
		System.out.println(new String(xmlBuffer));
		System.out.println(jsonObject);

		UserInfo result = new UserInfo();
		result.parsePbFrom(pbBuffer);
		result.parseXmlFrom(new String(xmlBuffer));
		result = SuperPojoManager.parseJsonFrom(jsonObject.toString(), UserInfo.class);
	}

	public void testWrite() {
		UserInfo user = new UserInfo();
		user.setId(1);
		user.setName("Feinno");
		user.setSex(UserInfo.SexEnum.FEMALE);
		user.setBirthday(new Date());
		user.setAddress("北京市朝阳区北苑路甲13号院北辰泰岳大厦18层");
		byte[] buffer = user.toPbByteArray();
		UserInfo user2 = new UserInfo();
		user2.parsePbFrom(buffer);

		List<UserInfo> list = new ArrayList<UserInfo>();
		list.add(user2);
		list.add(user2);
		list.add(user2);
		list.add(user2);
		user.setFriends(list);
		user.setBestFriend(user2);
		Map<String, UserInfo> map = new HashMap<String, UserInfo>();
		map.put("T1", user2);
		map.put("T2", user2);
		map.put("T3", user2);
		user.setFriendMap(map);

		buffer = user.toPbByteArray();
		UserInfo result = new UserInfo();
		result.parsePbFrom(buffer);
		System.out.println(new String(result.toXmlByteArray()));
		// System.out.println(result.toJsonObject());
		// System.out.println(Arrays.toString(result.toPbByteArray()));
		//
		// System.out.println("XML  length: " + result.toXmlByteArray().length);
		// System.out.println("JSON length: " +
		// result.toJsonObject().toString().getBytes().length);
		// System.out.println("PB   length: " + result.toPbByteArray().length);
	}

	public static class Bean extends SuperPojo {

		@Field(id = 1, type = NodeType.NODE)
		private Guid guid = null;

		@Field(id = 2)
		private Guid guid2 = null;

		public Guid getGuid() {
			return guid;
		}

		public void setGuid(Guid guid) {
			this.guid = guid;
		}

		public Guid getGuid2() {
			return guid2;
		}

		public void setGuid2(Guid guid2) {
			this.guid2 = guid2;
		}

	}
}
