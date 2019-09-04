package test.org.helium.superpojo.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.feinno.superpojo.type.Guid;

public class DataCreater {

	public static FullElementsBean newFullElementsBean(boolean isFill) {
		if (!isFill) {
			return new FullElementsBean();
		}
		FullElementsBean fullElementsBean = new FullElementsBean();
		// part 1
		fullElementsBean.setInt_Test(10);
		fullElementsBean.setLong_Test(100L);
		fullElementsBean.setFloat_Test(1000.1f);
		fullElementsBean.setDouble_Test(10000.1d);
		fullElementsBean.setBoolean_Test(true);
		short s1 = 126;
		byte b1 = 8;
		fullElementsBean.setShort_Test(s1);
		fullElementsBean.setByte_Test(b1);
		fullElementsBean.setChar_Test('a');
		// part 2
		fullElementsBean.setInteger_obj(20);
		fullElementsBean.setLong_obj(200L);
		fullElementsBean.setFloat_obj(2000.1f);
		fullElementsBean.setDouble_obj(20000.1d);
		fullElementsBean.setBoolean_obj(true);
		short s2 = 256;
		byte b2 = 16;
		fullElementsBean.setShort_obj(new Short(s2));
		fullElementsBean.setByte_obj(new Byte(b2));
		fullElementsBean.setChar_obj(new Character('b'));

		// part 3
		fullElementsBean.setStringTest("胃,\n你好吗？\n");

		FullElementsBean fullElementsBeanTemp = newFullElementsBeanTest(false);
		fullElementsBeanTemp.setStringTest("你好，我是来做内嵌测试的，看到我，说明内嵌成功！恭喜你！");

		fullElementsBean.setFullElementsBean(fullElementsBeanTemp);
		fullElementsBean.setTable(newTable(true));
		fullElementsBean.setGuid(Guid.randomGuid());
		fullElementsBean.setDate(new Date());
		fullElementsBean.setDateUTC(new Date());

		// part 4

		// 原始类型数组
		fullElementsBean.setInteger_Array(new int[] { 14, 15, 16 });

		fullElementsBean.setLong_Array(new long[] { 24L, 25L, 26L });

		fullElementsBean.setFloat_Array(new float[] { 34f, 35f, 36f });

		fullElementsBean.setDouble_Array(new double[] { 44d, 45d, 45d });

		fullElementsBean.setBoolean_Array(new boolean[] { false, true, false });

		fullElementsBean.setShort_Array(new short[] { 64, 65, 66 });

		fullElementsBean.setByte_Array(new byte[] { 74, 75, 76 });

		fullElementsBean.setChar_Array(new char[] { 'd', 'e', 'f' });

		// 包装类型数组
		fullElementsBean.setInteger_Object_Array(new Integer[] { 11, 12, 13 });

		fullElementsBean.setLong_Object_Array(new Long[] { 21L, 22L, 23L });

		fullElementsBean.setFloat_Object_Array(new Float[] { 31f, 32f, 33f });

		fullElementsBean.setDouble_Object_Array(new Double[] { 41d, 42d, 43d });

		fullElementsBean.setBoolean_Object_Array(new Boolean[] { true, false, true });

		fullElementsBean.setShort_Object_Array(new Short[] { 61, 62, 63 });

		fullElementsBean.setByte_Object_Array(new Byte[] { 71, 72, 73 });

		fullElementsBean.setCharacter_Object_Array(new Character[] { 'a', 'b', 'c' });

		fullElementsBean.setString_Object_Array(new String[] { "aaa", "bbb", "ccc" });

		fullElementsBean.setTable_Object_Array(new Table[] { newTable(true), newTable(false), newTable(true) });

		fullElementsBean.setDate_Object_Array(new Date[] { new Date(), new Date(), new Date() });
		fullElementsBean.setSql_date_Object_Array(new java.sql.Date[] { new java.sql.Date(System.currentTimeMillis()),
				new java.sql.Date(System.currentTimeMillis()), new java.sql.Date(System.currentTimeMillis()) });

		// part 5
		List<Integer> list1 = new ArrayList<Integer>();
		list1.add(11);
		list1.add(12);
		list1.add(13);
		fullElementsBean.setInteger_List(list1);

		List<Long> list2 = new ArrayList<Long>();
		for (int i = 0; i < 1; i++) {
			list2.add(21L);
		}
		list2.add(22L);
		list2.add(23L);
		fullElementsBean.setLong_List(list2);

		List<Float> list3 = new ArrayList<Float>();
		list3.add(Float.valueOf(31f));
		list3.add(32f);
		list3.add(33f);
		fullElementsBean.setFloat_List(list3);

		List<Double> list4 = new ArrayList<Double>();
		list4.add(41.1d);
		list4.add(41.2d);
		list4.add(41.3d);
		fullElementsBean.setDouble_List(list4);

		List<Boolean> list5 = new ArrayList<Boolean>();
		list5.add(true);
		list5.add(false);
		list5.add(true);
		fullElementsBean.setBoolean_List(list5);

		List<Short> list6 = new ArrayList<Short>();
		short s61 = 61;
		short s62 = 62;
		short s63 = 63;
		list6.add(s61);
		list6.add(s62);
		list6.add(s63);
		fullElementsBean.setShort_List(list6);

		List<Byte> list7 = new ArrayList<Byte>();
		byte b71 = 1;
		byte b72 = 2;
		byte b73 = 3;
		list7.add(b71);
		list7.add(b72);
		list7.add(b73);
		fullElementsBean.setByte_List(list7);

		List<Character> list8 = new ArrayList<Character>();
		list8.add('a');
		list8.add('b');
		list8.add('c');
		fullElementsBean.setCharacter_List(list8);

		List<String> list11 = new ArrayList<String>();
		list11.add("aaaaaaaaaa");
		list11.add("bbbbbbbbbb");
		list11.add("cccccccccc");
		fullElementsBean.setString_List(list11);

		List<FullElementsBean> list12 = new ArrayList<FullElementsBean>();
		list12.add(new FullElementsBean());
		list12.add(fullElementsBeanTemp);
		list12.add(new FullElementsBean());
		fullElementsBean.setFullElements_List(list12);

		List<Table> list13 = new ArrayList<Table>();
		list13.add(newTable(false));
		list13.add(newTable(false));
		list13.add(newTable(false));
		fullElementsBean.setTable_List(list13);

		List<ProtoComboDoubleString> list14 = new ArrayList<ProtoComboDoubleString>();
		list14.add(new ProtoComboDoubleString("aa", "aa"));
		list14.add(new ProtoComboDoubleString("bb", "bb"));
		list14.add(new ProtoComboDoubleString("cc", "cc"));
		fullElementsBean.setProtoComboDoubleString_List(list14);

		List<Date> list15 = new ArrayList<Date>();
		list15.add(new Date());
		list15.add(new Date());
		list15.add(new Date());
		fullElementsBean.setDate_List(list15);

		List<java.sql.Date> list16 = new ArrayList<java.sql.Date>();
		list16.add(new java.sql.Date(System.currentTimeMillis()));
		list16.add(new java.sql.Date(System.currentTimeMillis()));
		list16.add(new java.sql.Date(System.currentTimeMillis()));
		fullElementsBean.setSqlDate_List(list16);

		// PART6 MAP类型
		Map<String, String> map_S_S_Obj = new HashMap<String, String>();
		for (int i = 0; i < 1000; i++) {
			map_S_S_Obj.put(i + "key", i + "value");
		}
		fullElementsBean.setMap_S_S(map_S_S_Obj);

		Map<Integer, String> map_I_S_Obj = new HashMap<Integer, String>();
		for (int i = 0; i < 1000; i++) {
			map_I_S_Obj.put(i, i + "aaaa");
		}
		fullElementsBean.setMap_I_S(map_I_S_Obj);

		Map<String, Long> map_S_L_Obj = new HashMap<String, Long>();
		for (int i = 0; i < 1000; i++) {
			map_S_L_Obj.put(i + "key", Long.valueOf(i + "0000000"));
		}
		fullElementsBean.setMap_S_L(map_S_L_Obj);

		Random random = new Random();
		Map<Boolean, Character> map_B_C_Obj = new HashMap<Boolean, Character>();
		for (int i = 0; i < 1000; i++) {
			map_B_C_Obj.put(random.nextBoolean(), (char) (i + 97));
		}
		fullElementsBean.setMap_B_C(map_B_C_Obj);

		Map<String, FullElementsBean> map_S_Full_Obj = new HashMap<String, FullElementsBean>();
		for (int i = 0; i < 10; i++) {
			map_S_Full_Obj.put(i + "aaa", newFullElementsBeanTest(false));
		}
		fullElementsBean.setMap_S_Full(map_S_Full_Obj);

		fullElementsBean.setSqlDate(new java.sql.Date(System.currentTimeMillis()));

		return fullElementsBean;
	}

	public static FullElementsBean fillToFullElementsBean(FullElementsBean fullElementsBean) {
		// part 1
		fullElementsBean.setInt_Test(10);
		fullElementsBean.setLong_Test(100L);
		fullElementsBean.setFloat_Test(1000.1f);
		fullElementsBean.setDouble_Test(10000.1d);
		fullElementsBean.setBoolean_Test(true);
		short s1 = 126;
		byte b1 = 8;
		fullElementsBean.setShort_Test(s1);
		fullElementsBean.setByte_Test(b1);
		fullElementsBean.setChar_Test('a');
		// part 2
		fullElementsBean.setInteger_obj(20);
		fullElementsBean.setLong_obj(200L);
		fullElementsBean.setFloat_obj(2000.1f);
		fullElementsBean.setDouble_obj(20000.1d);
		fullElementsBean.setBoolean_obj(true);
		short s2 = 256;
		byte b2 = 16;
		fullElementsBean.setShort_obj(new Short(s2));
		fullElementsBean.setByte_obj(new Byte(b2));
		fullElementsBean.setChar_obj(new Character('b'));

		// part 3
		fullElementsBean.setStringTest("胃,\n你好吗？\n");

		FullElementsBean fullElementsBeanTemp = newFullElementsBeanTest(false);
		fullElementsBeanTemp.setStringTest("你好，我是来做内嵌测试的，看到我，说明内嵌成功！恭喜你！");

		fullElementsBean.setFullElementsBean(fullElementsBeanTemp);
		fullElementsBean.setTable(newTable(true));
		fullElementsBean.setGuid(Guid.randomGuid());
		fullElementsBean.setDate(new Date());
		fullElementsBean.setDateUTC(new Date());

		// part 4

		// 原始类型数组
		fullElementsBean.setInteger_Array(new int[] { 14, 15, 16 });

		fullElementsBean.setLong_Array(new long[] { 24L, 25L, 26L });

		fullElementsBean.setFloat_Array(new float[] { 34f, 35f, 36f });

		fullElementsBean.setDouble_Array(new double[] { 44d, 45d, 45d });

		fullElementsBean.setBoolean_Array(new boolean[] { false, true, false });

		fullElementsBean.setShort_Array(new short[] { 64, 65, 66 });

		fullElementsBean.setByte_Array(new byte[] { 74, 75, 76 });

		fullElementsBean.setChar_Array(new char[] { 'd', 'e', 'f' });

		// 包装类型数组
		fullElementsBean.setInteger_Object_Array(new Integer[] { 11, 12, 13 });

		fullElementsBean.setLong_Object_Array(new Long[] { 21L, 22L, 23L });

		fullElementsBean.setFloat_Object_Array(new Float[] { 31f, 32f, 33f });

		fullElementsBean.setDouble_Object_Array(new Double[] { 41d, 42d, 43d });

		fullElementsBean.setBoolean_Object_Array(new Boolean[] { true, false, true });

		fullElementsBean.setShort_Object_Array(new Short[] { 61, 62, 63 });

		fullElementsBean.setByte_Object_Array(new Byte[] { 71, 72, 73 });

		fullElementsBean.setCharacter_Object_Array(new Character[] { 'a', 'b', 'c' });

		fullElementsBean.setString_Object_Array(new String[] { "aaa", "bbb", "ccc" });

		fullElementsBean.setTable_Object_Array(new Table[] { newTable(true), newTable(false), newTable(true) });

		// part 5
		List<Integer> list1 = new ArrayList<Integer>();
		list1.add(11);
		list1.add(12);
		list1.add(13);
		fullElementsBean.setInteger_List(list1);

		List<Long> list2 = new ArrayList<Long>();
		for (int i = 0; i < 1; i++) {
			list2.add(21L);
		}
		list2.add(22L);
		list2.add(23L);
		fullElementsBean.setLong_List(list2);

		List<Float> list3 = new ArrayList<Float>();
		list3.add(Float.valueOf(31f));
		list3.add(32f);
		list3.add(33f);
		fullElementsBean.setFloat_List(list3);

		List<Double> list4 = new ArrayList<Double>();
		list4.add(41.1d);
		list4.add(41.2d);
		list4.add(41.3d);
		fullElementsBean.setDouble_List(list4);

		List<Boolean> list5 = new ArrayList<Boolean>();
		list5.add(true);
		list5.add(false);
		list5.add(true);
		fullElementsBean.setBoolean_List(list5);

		List<Short> list6 = new ArrayList<Short>();
		short s61 = 61;
		short s62 = 62;
		short s63 = 63;
		list6.add(s61);
		list6.add(s62);
		list6.add(s63);
		fullElementsBean.setShort_List(list6);

		List<Byte> list7 = new ArrayList<Byte>();
		byte b71 = 1;
		byte b72 = 2;
		byte b73 = 3;
		list7.add(b71);
		list7.add(b72);
		list7.add(b73);
		fullElementsBean.setByte_List(list7);

		List<Character> list8 = new ArrayList<Character>();
		list8.add('a');
		list8.add('b');
		list8.add('c');
		fullElementsBean.setCharacter_List(list8);

		List<String> list11 = new ArrayList<String>();
		list11.add("aaaaaaaaaa");
		list11.add("bbbbbbbbbb");
		list11.add("cccccccccc");
		fullElementsBean.setString_List(list11);

		List<FullElementsBean> list12 = new ArrayList<FullElementsBean>();
		list12.add(new FullElementsBean());
		list12.add(fullElementsBeanTemp);
		list12.add(new FullElementsBean());
		fullElementsBean.setFullElements_List(list12);

		List<Table> list13 = new ArrayList<Table>();
		list13.add(newTable(false));
		list13.add(newTable(false));
		list13.add(newTable(false));
		fullElementsBean.setTable_List(list13);

		List<ProtoComboDoubleString> list14 = new ArrayList<ProtoComboDoubleString>();
		list14.add(new ProtoComboDoubleString("aa", "aa"));
		list14.add(new ProtoComboDoubleString("bb", "bb"));
		list14.add(new ProtoComboDoubleString("cc", "cc"));
		fullElementsBean.setProtoComboDoubleString_List(list14);

		// PART6 MAP类型
		Map<String, String> map_S_S_Obj = new HashMap<String, String>();
		for (int i = 0; i < 1000; i++) {
			map_S_S_Obj.put(i + "key", i + "value");
		}
		fullElementsBean.setMap_S_S(map_S_S_Obj);

		Map<Integer, String> map_I_S_Obj = new HashMap<Integer, String>();
		for (int i = 0; i < 1000; i++) {
			map_I_S_Obj.put(i, i + "aaaa");
		}
		fullElementsBean.setMap_I_S(map_I_S_Obj);

		Map<String, Long> map_S_L_Obj = new HashMap<String, Long>();
		for (int i = 0; i < 1000; i++) {
			map_S_L_Obj.put(i + "key", Long.valueOf(i + "0000000"));
		}
		fullElementsBean.setMap_S_L(map_S_L_Obj);

		Random random = new Random();
		Map<Boolean, Character> map_B_C_Obj = new HashMap<Boolean, Character>();
		for (int i = 0; i < 1000; i++) {
			map_B_C_Obj.put(random.nextBoolean(), (char) (i + 97));
		}
		fullElementsBean.setMap_B_C(map_B_C_Obj);

		Map<String, FullElementsBean> map_S_Full_Obj = new HashMap<String, FullElementsBean>();
		for (int i = 0; i < 10; i++) {
			map_S_Full_Obj.put(i + "aaa", newFullElementsBeanTest(false));
		}
		fullElementsBean.setMap_S_Full(map_S_Full_Obj);

		fullElementsBean.setSqlDate(new java.sql.Date(System.currentTimeMillis()));

		return fullElementsBean;
	}

	private static FullElementsBean newFullElementsBeanTest(boolean isFill) {
		if (!isFill) {
			return new FullElementsBean();
		}
		FullElementsBean fullElementsBean = new FullElementsBean();
		// part 1
		fullElementsBean.setInt_Test(10);
		fullElementsBean.setLong_Test(100L);
		fullElementsBean.setFloat_Test(1000.1f);
		fullElementsBean.setDouble_Test(10000.1d);
		fullElementsBean.setBoolean_Test(true);
		short s1 = 126;
		byte b1 = 8;
		fullElementsBean.setShort_Test(s1);
		fullElementsBean.setByte_Test(b1);
		fullElementsBean.setChar_Test('a');
		// part 2
		fullElementsBean.setInteger_obj(20);
		fullElementsBean.setLong_obj(200L);
		fullElementsBean.setFloat_obj(2000.1f);
		fullElementsBean.setDouble_obj(20000.1d);
		fullElementsBean.setBoolean_obj(true);
		short s2 = 256;
		byte b2 = 16;
		fullElementsBean.setShort_obj(new Short(s2));
		fullElementsBean.setByte_obj(new Byte(b2));
		fullElementsBean.setChar_obj(new Character('b'));

		// part 3
		fullElementsBean.setStringTest("胃,你好吗？");

		FullElementsBean fullElementsBeanTemp = newFullElementsBean(false);
		fullElementsBeanTemp.setStringTest("你好，我是来做内嵌测试的，看到我，说明内嵌成功！恭喜你！");

		fullElementsBean.setFullElementsBean(fullElementsBeanTemp);
		fullElementsBean.setTable(newTable(false));
		fullElementsBean.setGuid(Guid.randomGuid());
		fullElementsBean.setDate(new Date());
		fullElementsBean.setDateUTC(new Date());

		// part 4
		// 原始类型数组
		fullElementsBean.setInteger_Array(new int[] { 14, 15, 16 });

		fullElementsBean.setLong_Array(new long[] { 24L, 25L, 26L });

		fullElementsBean.setFloat_Array(new float[] { 34f, 35f, 36f });

		fullElementsBean.setDouble_Array(new double[] { 44d, 45d, 45d });

		fullElementsBean.setBoolean_Array(new boolean[] { false, true, false });

		fullElementsBean.setShort_Array(new short[] { 64, 65, 66 });

		fullElementsBean.setByte_Array(new byte[] { 74, 75, 76 });

		fullElementsBean.setChar_Array(new char[] { 'd', 'e', 'f' });

		// 包装类型数组
		fullElementsBean.setInteger_Object_Array(new Integer[] { 11, 12, 13 });

		fullElementsBean.setLong_Object_Array(new Long[] { 21L, 22L, 23L });

		fullElementsBean.setFloat_Object_Array(new Float[] { 31f, 32f, 33f });

		fullElementsBean.setDouble_Object_Array(new Double[] { 41d, 42d, 43d });

		fullElementsBean.setBoolean_Object_Array(new Boolean[] { true, false, true });

		fullElementsBean.setShort_Object_Array(new Short[] { 61, 62, 63 });

		fullElementsBean.setByte_Object_Array(new Byte[] { 71, 72, 73 });

		fullElementsBean.setCharacter_Object_Array(new Character[] { 'a', 'b', 'c' });

		fullElementsBean.setString_Object_Array(new String[] { "aaa", "bbb", "ccc" });

		fullElementsBean.setTable_Object_Array(new Table[] { newTable(true), newTable(false), newTable(true) });

		// part 5
		List<Integer> list1 = new ArrayList<Integer>();
		list1.add(11);
		list1.add(12);
		list1.add(13);
		fullElementsBean.setInteger_List(list1);

		List<Long> list2 = new ArrayList<Long>();
		for (int i = 0; i < 1; i++) {
			list2.add(21L);
		}
		list2.add(22L);
		list2.add(23L);
		fullElementsBean.setLong_List(list2);

		List<Float> list3 = new ArrayList<Float>();
		list3.add(Float.valueOf(31f));
		list3.add(32f);
		list3.add(33f);
		fullElementsBean.setFloat_List(list3);

		List<Double> list4 = new ArrayList<Double>();
		list4.add(41.1d);
		list4.add(41.2d);
		list4.add(41.3d);
		fullElementsBean.setDouble_List(list4);

		List<Boolean> list5 = new ArrayList<Boolean>();
		list5.add(true);
		list5.add(false);
		list5.add(true);
		fullElementsBean.setBoolean_List(list5);

		List<Short> list6 = new ArrayList<Short>();
		short s61 = 61;
		short s62 = 62;
		short s63 = 63;
		list6.add(s61);
		list6.add(s62);
		list6.add(s63);
		fullElementsBean.setShort_List(list6);

		List<Byte> list7 = new ArrayList<Byte>();
		byte b71 = 1;
		byte b72 = 2;
		byte b73 = 3;
		list7.add(b71);
		list7.add(b72);
		list7.add(b73);
		fullElementsBean.setByte_List(list7);

		List<Character> list8 = new ArrayList<Character>();
		list8.add('a');
		list8.add('b');
		list8.add('c');
		fullElementsBean.setCharacter_List(list8);

		List<String> list11 = new ArrayList<String>();
		list11.add("aaaaaaaaaa");
		list11.add("bbbbbbbbbb");
		list11.add("cccccccccc");
		fullElementsBean.setString_List(list11);

		List<FullElementsBean> list12 = new ArrayList<FullElementsBean>();
		list12.add(new FullElementsBean());
		list12.add(fullElementsBeanTemp);
		list12.add(new FullElementsBean());
		fullElementsBean.setFullElements_List(list12);

		List<Table> list13 = new ArrayList<Table>();
		list13.add(newTable(false));
		list13.add(newTable(false));
		list13.add(newTable(false));
		fullElementsBean.setTable_List(list13);

		List<ProtoComboDoubleString> list14 = new ArrayList<ProtoComboDoubleString>();
		list14.add(new ProtoComboDoubleString("aa", "aa"));
		list14.add(new ProtoComboDoubleString("bb", "bb"));
		list14.add(new ProtoComboDoubleString("cc", "cc"));
		fullElementsBean.setProtoComboDoubleString_List(list14);

		// PART6 MAP类型
		Map<String, String> map_S_S_Obj = new HashMap<String, String>();
		for (int i = 0; i < 1000; i++) {
			map_S_S_Obj.put(i + "key", i + "value");
		}
		fullElementsBean.setMap_S_S(map_S_S_Obj);

		Map<Integer, String> map_I_S_Obj = new HashMap<Integer, String>();
		for (int i = 0; i < 1000; i++) {
			map_I_S_Obj.put(i, i + "aaaa");
		}
		fullElementsBean.setMap_I_S(map_I_S_Obj);

		Map<String, Long> map_S_L_Obj = new HashMap<String, Long>();
		for (int i = 0; i < 1000; i++) {
			map_S_L_Obj.put(i + "key", Long.valueOf(i + "0000000"));
		}
		fullElementsBean.setMap_S_L(map_S_L_Obj);

		Random random = new Random();
		Map<Boolean, Character> map_B_C_Obj = new HashMap<Boolean, Character>();
		for (int i = 0; i < 1000; i++) {
			map_B_C_Obj.put(random.nextBoolean(), (char) (i + 97));
		}
		fullElementsBean.setMap_B_C(map_B_C_Obj);
		fullElementsBean.setSqlDate(new java.sql.Date(System.currentTimeMillis()));
		return fullElementsBean;
	}

	public static Table newTable(boolean isFill) {
		if (!isFill) {
			return new Table();
		}
		Table table = new Table();
		table.setId(1);
		table.setName("我是Table");
		table.setEmail("Table@163.com");
		User user = new User();
		user.setId(2);
		user.setName("我是User");
		user.setEmail("User@163.com");
		table.setUser(user);
		return table;
	}

	public static Table fillToTable(Table table) {
		table.setId(1);
		table.setName("我是Table");
		table.setEmail("Table@163.com");
		User user = new User();
		user.setId(2);
		user.setName("我是User");
		user.setEmail("User@163.com");
		table.setUser(user);
		return table;
	}

	public static User fillToUser(User user) {
		user.setId(1);
		user.setName("我是Table");
		user.setEmail("Table@163.com");
		return user;
	}

	public static User newUser(boolean isFill) {
		if (!isFill) {
			return new User();
		}
		User user = new User();
		user.setId(1);
		user.setName("我是Table");
		user.setEmail("Table@163.com");
		return user;
	}

	public static ParentBean.ChildBean newChildrenBean(boolean isFill) {
		if (!isFill) {
			return new ParentBean.ChildBean();
		}
		ParentBean.ChildBean bean = new ParentBean.ChildBean();
		bean.setId(2);
		bean.setChildName("Child");
		bean.setChildLength(180);
		bean.setChildTable(newTable(true));
		bean.setParentName("Parent");
		bean.setParentLength(190);
		bean.setParentTable(newTable(true));
		return bean;
	}

}
