package test.org.helium.superpojo.bean;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.*;
import com.feinno.superpojo.type.Guid;

import java.util.Date;
import java.util.List;
import java.util.Map;

//import com.feinno.superpojo.type.Guid;
//@Entity()
public class FullElementsBean extends SuperPojo {

	// part 1 基本数据类型
	@Field(id = 101, type = NodeType.ATTR)
	private int int_Test;

	@Field(id = 102, type = NodeType.ATTR)
	private long long_Test;

	@Field(id = 103, name = "float")
	private float float_Test;

	@Field(id = 104, name = "double")
	private double double_Test;

	@Field(id = 105)
	private boolean boolean_Test;

	@Field(id = 106)
	private short short_Test;

	@Field(id = 107)
	private byte byte_Test;

	// @Field(id = 108)
	private char char_Test;

	// part 2 包装数据类型
	@Field(id = 201)
	private Integer Integer_obj;

	@Field(id = 202)
	private Long Long_obj;

	@Field(id = 203)
	private Float Float_obj;

	@Field(id = 204)
	private Double Double_obj;

	@Field(id = 205)
	private Boolean Boolean_obj;

	@Field(id = 206)
	private Short short_obj;

	@Field(id = 207)
	private Byte byte_obj;

	@Field(id = 208)
	private Character char_obj;

	// part 3 对象类型
	@Field(id = 301)
	private String StringTest;

	@Field(id = 303)
	@FieldExtensions(newParentNode = "parentNode")
	private FullElementsBean fullElementsBean;

	@Field(id = 304)
	private Table table;

	@Field(id = 305)
	private Guid guid;

	@Field(id = 307)
	private Date date;

	@Field(id = 308, timeZone = TimeZone.UTC)
	private Date dateUTC;

	// part 4 数组类型
	@Field(id = 401)
	@FieldExtensions(newParentNode = "intArrayParentNode")
	private int[] Integer_Array;

	// @Field(id = 402)
	@Childs(id = 402, child = "longArrayElement")
	private long[] Long_Array;

	// @Field(id = 403)
	@Childs(id = 403, child = "floatElement", parent = "floatArray")
	private float[] Float_Array;

	@Field(id = 404)
	private double[] Double_Array;

	@Field(id = 405)
	private boolean[] Boolean_Array;

	@Field(id = 406)
	private short[] Short_Array;

	@Field(id = 407)
	private byte[] Byte_Array;

	@Field(id = 408)
	private char[] Char_Array;

	@Field(id = 409)
	@FieldExtensions(newParentNode = "intObjArrayParentNode")
	private Integer[] Integer_Object_Array;

	// @Field(id = 410)
	@Childs(id = 410, child = "longObjElement")
	private Long[] Long_Object_Array;

	@Field(id = 411)
	@Childs(id = 411, child = "floatObjElement", parent = "floatObjArray")
	private Float[] Float_Object_Array;

	@Field(id = 412)
	private Double[] Double_Object_Array;

	@Field(id = 413)
	private Boolean[] Boolean_Object_Array;

	@Field(id = 414)
	private Short[] Short_Object_Array;

	@Field(id = 415)
	private Byte[] Byte_Object_Array;

	@Field(id = 416)
	private Character[] Character_Object_Array;

	@Field(id = 417)
	private String[] String_Object_Array;

	@Field(id = 419)
	private Table[] Table_Object_Array;

	@Field(id = 420)
	private Date[] date_Object_Array;

	@Field(id = 421)
	private java.sql.Date[] sql_date_Object_Array;

	// part 5 集合类型
	@Field(id = 501)
	@FieldExtensions(newParentNode = "intObjListParentNode")
	private List<Integer> Integer_List;

	// @Field(id = 502)
	@Childs(id = 502, child = "longListElement")
	private List<Long> Long_List;

	// @Field(id = 503)
	@Childs(id = 502, child = "floatListElement", parent = "floatList")
	private List<Float> Float_List;

	@Field(id = 504)
	private List<Double> Double_List;

	@Field(id = 505)
	private List<Boolean> Boolean_List;

	@Field(id = 506)
	private List<Short> Short_List;

	@Field(id = 507)
	private List<Byte> Byte_List;

	@Field(id = 508)
	private List<Character> Character_List;

	@Field(id = 510)
	private List<String> String_List;

	@Field(id = 511)
	private List<FullElementsBean> FullElements_List;

	@Field(id = 512)
	private List<Table> Table_List;

	@Field(id = 513)
	private List<ProtoComboDoubleString> ProtoComboDoubleString_List;

	@Field(id = 514)
	private List<Date> date_List;

	@Field(id = 515)
	private List<java.sql.Date> sqlDate_List;

	// PART6 MAP类型
	@Field(id = 601)
	@FieldExtensions(newParentNode = "mapSSParent")
	private Map<String, String> map_S_S;

	// @Field(id = 602)
	@Childs(id = 602, child = "mapISElement")
	private Map<Integer, String> map_I_S;

	// @Field(id = 603)
	@Childs(id = 603, child = "mapSLElement", parent = "mapSLParent")
	private Map<String, Long> map_S_L;

	@Field(id = 605)
	private Map<Boolean, Character> map_B_C;

	@Field(id = 607)
	private Map<String, FullElementsBean> map_S_Full;

	// 这是未知字段，无法序列化的，但是被赋予了序列化序号
	@Field(id = 701)
	private java.sql.Date sqlDate;

	public int getInt_Test() {
		return int_Test;
	}

	public void setInt_Test(int int_Test) {
		this.int_Test = int_Test;
	}

	public long getLong_Test() {
		return long_Test;
	}

	public void setLong_Test(long long_Test) {
		this.long_Test = long_Test;
	}

	public float getFloat_Test() {
		return float_Test;
	}

	public void setFloat_Test(float float_Test) {
		this.float_Test = float_Test;
	}

	public double getDouble_Test() {
		return double_Test;
	}

	public void setDouble_Test(double double_Test) {
		this.double_Test = double_Test;
	}

	public boolean isBoolean_Test() {
		return boolean_Test;
	}

	public void setBoolean_Test(boolean boolean_Test) {
		this.boolean_Test = boolean_Test;
	}

	public short getShort_Test() {
		return short_Test;
	}

	public void setShort_Test(short short_Test) {
		this.short_Test = short_Test;
	}

	public byte getByte_Test() {
		return byte_Test;
	}

	public void setByte_Test(byte byte_Test) {
		this.byte_Test = byte_Test;
	}

	public char getChar_Test() {
		return char_Test;
	}

	public void setChar_Test(char char_Test) {
		this.char_Test = char_Test;
	}

	public Integer getInteger_obj() {
		return Integer_obj;
	}

	public void setInteger_obj(Integer integer_obj) {
		Integer_obj = integer_obj;
	}

	public Long getLong_obj() {
		return Long_obj;
	}

	public void setLong_obj(Long long_obj) {
		Long_obj = long_obj;
	}

	public Float getFloat_obj() {
		return Float_obj;
	}

	public void setFloat_obj(Float float_obj) {
		Float_obj = float_obj;
	}

	public Double getDouble_obj() {
		return Double_obj;
	}

	public void setDouble_obj(Double double_obj) {
		Double_obj = double_obj;
	}

	public Boolean getBoolean_obj() {
		return Boolean_obj;
	}

	public void setBoolean_obj(Boolean boolean_obj) {
		Boolean_obj = boolean_obj;
	}

	public Short getShort_obj() {
		return short_obj;
	}

	public void setShort_obj(Short short_obj) {
		this.short_obj = short_obj;
	}

	public Byte getByte_obj() {
		return byte_obj;
	}

	public void setByte_obj(Byte byte_obj) {
		this.byte_obj = byte_obj;
	}

	public Character getChar_obj() {
		return char_obj;
	}

	public void setChar_obj(Character char_obj) {
		this.char_obj = char_obj;
	}

	public String getStringTest() {
		return StringTest;
	}

	public void setStringTest(String stringTest) {
		StringTest = stringTest;
	}

	public FullElementsBean getFullElementsBean() {
		return fullElementsBean;
	}

	public void setFullElementsBean(FullElementsBean fullElementsBean) {
		this.fullElementsBean = fullElementsBean;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public Guid getGuid() {
		return guid;
	}

	public void setGuid(Guid guid) {
		this.guid = guid;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDateUTC() {
		return dateUTC;
	}

	public void setDateUTC(Date dateUTC) {
		this.dateUTC = dateUTC;
	}

	public int[] getInteger_Array() {
		return Integer_Array;
	}

	public void setInteger_Array(int[] integer_Array) {
		Integer_Array = integer_Array;
	}

	public long[] getLong_Array() {
		return Long_Array;
	}

	public void setLong_Array(long[] long_Array) {
		Long_Array = long_Array;
	}

	public float[] getFloat_Array() {
		return Float_Array;
	}

	public void setFloat_Array(float[] float_Array) {
		Float_Array = float_Array;
	}

	public double[] getDouble_Array() {
		return Double_Array;
	}

	public void setDouble_Array(double[] double_Array) {
		Double_Array = double_Array;
	}

	public boolean[] getBoolean_Array() {
		return Boolean_Array;
	}

	public void setBoolean_Array(boolean[] boolean_Array) {
		Boolean_Array = boolean_Array;
	}

	public short[] getShort_Array() {
		return Short_Array;
	}

	public void setShort_Array(short[] short_Array) {
		Short_Array = short_Array;
	}

	public byte[] getByte_Array() {
		return Byte_Array;
	}

	public void setByte_Array(byte[] byte_Array) {
		Byte_Array = byte_Array;
	}

	public char[] getChar_Array() {
		return Char_Array;
	}

	public void setChar_Array(char[] char_Array) {
		Char_Array = char_Array;
	}

	public Integer[] getInteger_Object_Array() {
		return Integer_Object_Array;
	}

	public void setInteger_Object_Array(Integer[] integer_Object_Array) {
		Integer_Object_Array = integer_Object_Array;
	}

	public Long[] getLong_Object_Array() {
		return Long_Object_Array;
	}

	public void setLong_Object_Array(Long[] long_Object_Array) {
		Long_Object_Array = long_Object_Array;
	}

	public Float[] getFloat_Object_Array() {
		return Float_Object_Array;
	}

	public void setFloat_Object_Array(Float[] float_Object_Array) {
		Float_Object_Array = float_Object_Array;
	}

	public Double[] getDouble_Object_Array() {
		return Double_Object_Array;
	}

	public void setDouble_Object_Array(Double[] double_Object_Array) {
		Double_Object_Array = double_Object_Array;
	}

	public Boolean[] getBoolean_Object_Array() {
		return Boolean_Object_Array;
	}

	public void setBoolean_Object_Array(Boolean[] boolean_Object_Array) {
		Boolean_Object_Array = boolean_Object_Array;
	}

	public Short[] getShort_Object_Array() {
		return Short_Object_Array;
	}

	public void setShort_Object_Array(Short[] short_Object_Array) {
		Short_Object_Array = short_Object_Array;
	}

	public Byte[] getByte_Object_Array() {
		return Byte_Object_Array;
	}

	public void setByte_Object_Array(Byte[] byte_Object_Array) {
		Byte_Object_Array = byte_Object_Array;
	}

	public Character[] getCharacter_Object_Array() {
		return Character_Object_Array;
	}

	public void setCharacter_Object_Array(Character[] character_Object_Array) {
		Character_Object_Array = character_Object_Array;
	}

	public String[] getString_Object_Array() {
		return String_Object_Array;
	}

	public void setString_Object_Array(String[] string_Object_Array) {
		String_Object_Array = string_Object_Array;
	}

	public Table[] getTable_Object_Array() {
		return Table_Object_Array;
	}

	public void setTable_Object_Array(Table[] table_Object_Array) {
		Table_Object_Array = table_Object_Array;
	}

	public List<Integer> getInteger_List() {
		return Integer_List;
	}

	public void setInteger_List(List<Integer> integer_List) {
		Integer_List = integer_List;
	}

	public List<Long> getLong_List() {
		return Long_List;
	}

	public void setLong_List(List<Long> long_List) {
		Long_List = long_List;
	}

	public List<Float> getFloat_List() {
		return Float_List;
	}

	public void setFloat_List(List<Float> float_List) {
		Float_List = float_List;
	}

	public List<Double> getDouble_List() {
		return Double_List;
	}

	public void setDouble_List(List<Double> double_List) {
		Double_List = double_List;
	}

	public List<Boolean> getBoolean_List() {
		return Boolean_List;
	}

	public void setBoolean_List(List<Boolean> boolean_List) {
		Boolean_List = boolean_List;
	}

	public List<Short> getShort_List() {
		return Short_List;
	}

	public void setShort_List(List<Short> short_List) {
		Short_List = short_List;
	}

	public List<Byte> getByte_List() {
		return Byte_List;
	}

	public void setByte_List(List<Byte> byte_List) {
		Byte_List = byte_List;
	}

	public List<Character> getCharacter_List() {
		return Character_List;
	}

	public void setCharacter_List(List<Character> character_List) {
		Character_List = character_List;
	}

	public List<String> getString_List() {
		return String_List;
	}

	public void setString_List(List<String> string_List) {
		String_List = string_List;
	}

	public List<FullElementsBean> getFullElements_List() {
		return FullElements_List;
	}

	public void setFullElements_List(List<FullElementsBean> fullElements_List) {
		FullElements_List = fullElements_List;
	}

	public List<Table> getTable_List() {
		return Table_List;
	}

	public void setTable_List(List<Table> table_List) {
		Table_List = table_List;
	}

	public List<ProtoComboDoubleString> getProtoComboDoubleString_List() {
		return ProtoComboDoubleString_List;
	}

	public void setProtoComboDoubleString_List(List<ProtoComboDoubleString> protoComboDoubleString_List) {
		ProtoComboDoubleString_List = protoComboDoubleString_List;
	}

	public Map<String, String> getMap_S_S() {
		return map_S_S;
	}

	public void setMap_S_S(Map<String, String> map_S_S) {
		this.map_S_S = map_S_S;
	}

	public Map<Integer, String> getMap_I_S() {
		return map_I_S;
	}

	public void setMap_I_S(Map<Integer, String> map_I_S) {
		this.map_I_S = map_I_S;
	}

	public Map<String, Long> getMap_S_L() {
		return map_S_L;
	}

	public void setMap_S_L(Map<String, Long> map_S_L) {
		this.map_S_L = map_S_L;
	}

	public Map<Boolean, Character> getMap_B_C() {
		return map_B_C;
	}

	public void setMap_B_C(Map<Boolean, Character> map_B_C) {
		this.map_B_C = map_B_C;
	}

	public Map<String, FullElementsBean> getMap_S_Full() {
		return map_S_Full;
	}

	public void setMap_S_Full(Map<String, FullElementsBean> map_S_Full) {
		this.map_S_Full = map_S_Full;
	}

	public java.sql.Date getSqlDate() {
		return sqlDate;
	}

	public void setSqlDate(java.sql.Date sqlDate) {
		this.sqlDate = sqlDate;
	}

	public Date[] getDate_Object_Array() {
		return date_Object_Array;
	}

	public void setDate_Object_Array(Date[] date_Object_Array) {
		this.date_Object_Array = date_Object_Array;
	}

	public java.sql.Date[] getSql_date_Object_Array() {
		return sql_date_Object_Array;
	}

	public void setSql_date_Object_Array(java.sql.Date[] sql_date_Object_Array) {
		this.sql_date_Object_Array = sql_date_Object_Array;
	}

	public List<Date> getDate_List() {
		return date_List;
	}

	public void setDate_List(List<Date> date_List) {
		this.date_List = date_List;
	}

	public List<java.sql.Date> getSqlDate_List() {
		return sqlDate_List;
	}

	public void setSqlDate_List(List<java.sql.Date> sqlDate_List) {
		this.sqlDate_List = sqlDate_List;
	}

}
