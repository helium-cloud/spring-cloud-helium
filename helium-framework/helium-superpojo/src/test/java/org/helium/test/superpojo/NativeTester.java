package org.helium.test.superpojo;

import com.feinno.superpojo.SuperPojoManager;
import com.feinno.superpojo.type.Flags;
import com.google.gson.JsonObject;
import org.helium.test.superpojo.bean.DataCreater;
import org.helium.test.superpojo.bean.Table;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class NativeTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		 new NativeTester().testInt();
		 new NativeTester().testInteger();
		 new NativeTester().testLong();
		 new NativeTester().testDouble();
		 new NativeTester().testBoolean();
		 new NativeTester().testByte();
		 new NativeTester().testList();
		 new NativeTester().testMap();
		 new NativeTester().testFlags();
	}

	@Test
	public void testInt() {
		int value = 18;

		JsonObject json = SuperPojoManager.toJsonObject(value);
		String xml = new String(SuperPojoManager.toXmlByteArray(value));
		byte[] pb = SuperPojoManager.toPbByteArray(value);

		Assert.assertEquals(json.get("integer1").getAsInt(), value);

		int resultXML = SuperPojoManager.parseXmlFrom(xml, 0);
		Assert.assertEquals(json.get("integer1").getAsInt(), resultXML);

		int resultPb = SuperPojoManager.parsePbFrom(pb, 0);
		Assert.assertEquals(json.get("integer1").getAsInt(), resultPb);

	}

	@Test
	public void testList() throws IOException {
		List<Integer> intList = new ArrayList<Integer>();
		intList.add(1);
		intList.add(2);
		intList.add(3);
		intList.add(4);
		intList.add(5);

		JsonObject json = SuperPojoManager.toJsonObject(intList, Integer.class);
		String xml = new String(SuperPojoManager.toXmlByteArray(intList, Integer.class));
		byte[] pb = SuperPojoManager.toPbByteArray(intList, Integer.class);
		System.out.println(json);

		List<Integer> resultList = new ArrayList<Integer>();
		resultList = SuperPojoManager.parseXmlFrom(xml, resultList, Integer.class);
		Assert.assertEquals(intList, resultList);

		resultList = new ArrayList<Integer>();
		resultList = SuperPojoManager.parsePbFrom(pb, resultList, Integer.class);
		Assert.assertEquals(intList, resultList);

	}

	@Test
	public void testMap() throws IOException {
		Map<String, Table> map = new HashMap<String, Table>();
		map.put("A", DataCreater.newTable(true));
		map.put("B", DataCreater.newTable(true));
		map.put("C", DataCreater.newTable(true));
		map.put("D", DataCreater.newTable(true));
		map.put("E", DataCreater.newTable(true));

		// JsonObject json = SuperPojoManager.toJsonObject(map, String.class,
		// Table.class);
		String xml = new String(SuperPojoManager.toXmlByteArray(map, String.class, Table.class));
		byte[] pb = SuperPojoManager.toPbByteArray(map, String.class, Table.class);
		// System.out.println(json);

		System.out.println(Arrays.toString(pb));

		Map<String, Table> resultMap = new HashMap<String, Table>();
		resultMap = SuperPojoManager.parseXmlFrom(xml, resultMap, String.class, Table.class);
		Assert.assertEquals(resultMap, resultMap);
		//
		resultMap = new HashMap<String, Table>();
		resultMap = SuperPojoManager.parsePbFrom(pb, resultMap, String.class, Table.class);
		//Assert.assertEquals(map, resultMap);

	}

	@Test
	public void testInteger() {
		Integer value = 18;

		JsonObject json = SuperPojoManager.toJsonObject(value);
		String xml = new String(SuperPojoManager.toXmlByteArray(value));
		byte[] pb = SuperPojoManager.toPbByteArray(value);

		Assert.assertEquals((int) json.get("integer1").getAsInt(), (int) value);

		int resultXML = SuperPojoManager.parseXmlFrom(xml, new Integer(0));
		Assert.assertEquals(json.get("integer1").getAsInt(), resultXML);

		int resultPb = SuperPojoManager.parsePbFrom(pb, new Integer(0));
		Assert.assertEquals(json.get("integer1").getAsInt(), resultPb);

	}

	@Test
	public void testLong() {
		long value = 18L;

		JsonObject json = SuperPojoManager.toJsonObject(value);
		String xml = new String(SuperPojoManager.toXmlByteArray(value));
		byte[] pb = SuperPojoManager.toPbByteArray(value);

		Assert.assertEquals(json.get("long1").getAsLong(), value);

		long resultXML = SuperPojoManager.parseXmlFrom(xml, 0L);
		Assert.assertEquals(json.get("long1").getAsLong(), resultXML);

		long resultPb = SuperPojoManager.parsePbFrom(pb, 0);
		Assert.assertEquals(json.get("long1").getAsLong(), resultPb);

	}

	@Test
	public void testDouble() {
		double value = 18.88D;

		JsonObject json = SuperPojoManager.toJsonObject(value);
		String xml = new String(SuperPojoManager.toXmlByteArray(value));
		byte[] pb = SuperPojoManager.toPbByteArray(value);

		Assert.assertEquals(json.get("double1").getAsDouble(), value, 0.0001);

		double resultXML = SuperPojoManager.parseXmlFrom(xml, 0D);
		Assert.assertEquals(json.get("double1").getAsDouble(), resultXML, 0.0001);

		double resultPb = SuperPojoManager.parsePbFrom(pb, 0D);
		Assert.assertEquals(json.get("double1").getAsDouble(), resultPb, 0.0001);

	}

	@Test
	public void testBoolean() {
		boolean value = true;

		JsonObject json = SuperPojoManager.toJsonObject(value);
		String xml = new String(SuperPojoManager.toXmlByteArray(value));
		byte[] pb = SuperPojoManager.toPbByteArray(value);

		Assert.assertEquals(json.get("boolean1").getAsBoolean(), value);

		boolean resultXML = SuperPojoManager.parseXmlFrom(xml, false);
		Assert.assertEquals(json.get("boolean1").getAsBoolean(), resultXML);

		boolean resultPb = SuperPojoManager.parsePbFrom(pb, false);
		Assert.assertEquals(json.get("boolean1").getAsBoolean(), resultPb);

	}

	@Test
	public void testByte() {
		byte value = 0x7C;

		JsonObject json = SuperPojoManager.toJsonObject(value);
		String xml = new String(SuperPojoManager.toXmlByteArray(value));
		byte[] pb = SuperPojoManager.toPbByteArray(value);

		Assert.assertEquals(json.get("byte1").getAsInt(), value);

		byte resultXML = SuperPojoManager.parseXmlFrom(xml, (byte) 0x00);
		Assert.assertEquals(json.get("byte1").getAsInt(), resultXML);

		byte resultPb = SuperPojoManager.parsePbFrom(pb, (byte) 0x00);
		Assert.assertEquals(json.get("byte1").getAsInt(), resultPb);

	}

	@Test
	public void testFlags() {
		Flags<UserInfo.SexEnum> sexFlags = new Flags<UserInfo.SexEnum>(UserInfo.SexEnum.FEMALE);

		Flags<UserInfo.SexEnum> resultFlags = new Flags<UserInfo.SexEnum>(0);
		String xml = new String(SuperPojoManager.toXmlByteArray(sexFlags));
		resultFlags = SuperPojoManager.parseXmlFrom(xml, resultFlags);
		Assert.assertTrue(resultFlags.has(UserInfo.SexEnum.FEMALE));
	}
}
