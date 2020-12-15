package org.helium.test.superpojo.bean;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;
import com.feinno.superpojo.generator.CodeGenerator;
import com.feinno.superpojo.type.EnumInteger;
import com.feinno.superpojo.util.SuperPojoUtils;

/**
 * 
 * 
 * @author Lv.Mingwei
 * 
 */
public class AnyNodeTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		CodeGenerator.setDebug(true);
		CodeGenerator.setSourcePath("/tmp/java");

		UserInfo user = new UserInfo();
		user.setId(1);
		user.setName("Feinno");
		user.setSex(SexEnum.MALE);
		System.out.println(new String(user.toXmlByteArray()));

		StringBuffer xmlBUffer = new StringBuffer();
		xmlBUffer.append("<UserInfo id='1' name='Feinno' sex='0'>");
		xmlBUffer.append("  <Contanct id='1'>");
		xmlBUffer.append("    <name>Tom</name>");
		xmlBUffer.append("    <telphone>138xxxxxxxx</telphone>");
		xmlBUffer.append("  </Contanct>");
		xmlBUffer.append("</UserInfo>");

		user = new UserInfo();
		user.parseXmlFrom(xmlBUffer.toString());
		System.out.println(new String(user.toXmlByteArray()));
		Contanct contanct = SuperPojoUtils.getAnyNode(user).convertTo(Contanct.class);
		System.out.println(contanct.getId());
		System.out.println(contanct.getName());
		System.out.println(contanct.getTelphone());

		// byte[] buffer = user.toPbByteArray();
		// user = new UserInfo();
		// user.parsePbFrom(buffer);
		// System.out.println(new String(user.toXmlByteArray()));
		// contanct = SuperPojoUtils.getAnyNode(user).convertTo(Contanct.class);
		// System.out.println(contanct.getId());
		// System.out.println(contanct.getName());
		// System.out.println(contanct.getTelphone());
		// ////////////////////////////////////////////////////////////////////

		xmlBUffer = new StringBuffer();
		xmlBUffer.append("<UserInfo id='1' name='Feinno' sex='0'>");
		xmlBUffer.append("  fdsafdsafdsa ");
		xmlBUffer.append("</UserInfo>");
		user = new UserInfo();
		user.parseXmlFrom(xmlBUffer.toString());
		String str = SuperPojoUtils.getStringAnyNode(user);
		System.out.println(str);
	}

	/**
	 * 约束： AnyNode下面只允许两种情况， 1. NodeType只允许INNER 2. 允许字符串值作为InnerText
	 * node.getInnerText();
	 * 
	 * 3. 允许单节点，存放AnyNode的根节点，只允许有Attribute， <Node><AnyNode></AnyNode></Node>
	 * Node node = node.getInnerNode(Class<? extends SuperPojo> nodeClass);
	 * 
	 * 4. AnyNode下存放的Protobuf必须标注@Entity根节点声明，且结点名称与AnyNode内一致
	 * 
	 * <pre>
	 *  <UserInfo id=1,name='Feinno',sex=''>
	 *  	<Contacts>
	 *  	<Contanct id=1>
	 *  		<name>Tom</name>
	 *  		<telphone>138xxxxxxxx</telphone>
	 *  	</Contanct>
	 *  	<Contanct id=1>
	 *  		<name>Tom</name>
	 *  		<telphone>138xxxxxxxx</telphone>
	 *  	</Contanct>
	 *  	<Contacts>
	 *  </UserInfo>
	 * </pre>
	 * 
	 * # 1
	 * 
	 * @Field(id = 4, type = NodeType.Node, name = "Contact") private
	 *           ContactNode contact;
	 * 
	 *           # 2
	 * @Field(id = 4, type = NodeType.INNER) private AnyNode node; //
	 *           <root><Contact id=1>...</Contact></root> // number(4) << 2 |
	 *           type(2) LEN(ContactNode) buffer[10 01 20 02 a9 09 90 b2 20]
	 * 
	 *           tag 4 << 3|2 LENGTH [tag 1 << 3 | 2 object | tag 1 << 3 | 2
	 *           object | tag 1 << 3 | 2 object | tag 1 << 3 | 2 object]
	 * 
	 *           class ContactNodeInner {
	 * @Field(id = 1, type = NodeType.Node, name = "Contact") private
	 *           ContactNode contact; }
	 * 
	 *           buffer[10 01 20 02] message Contact { required int id = 1;
	 *           required string name =2; }
	 * 
	 *           buffer[10 01 20 02] message User { required string name = 1;
	 *           required string passwd = 2; } node.convertTo();
	 * 
	 *           <pre>
	 *  <UserInfo id="1",name='Feinno',sex=''>1028</UserInfo>
	 * </pre>
	 * 
	 * @Field(id = 4, type = NodeType.INNER) private String s;
	 * 
	 * @Field(id = 4, type = NoteType.INNER) private AnyNode node; //
	 *           <UserInfo>1028</UserInfo> // number(4) << 3 | type(2) LEN(4)
	 *           1028
	 * 
	 *           node.getText(); String
	 * 
	 * 
	 * 
	 * @author Lv.Mingwei
	 * 
	 */
	public static class UserInfo extends SuperPojo {

		@Field(id = 1, type = NodeType.ATTR)
		// 08 01
		private int id;

		@Field(id = 2, type = NodeType.ATTR)
		// 12 04 38 38 39 40
		private String name;

		@Field(id = 3, type = NodeType.ATTR)
		// 20 02
		private SexEnum sex;

		// @Field(id = 4, type = NoteType.INNER) // 42 20
		// "09 90 28 10 29 ab ..." len = 20
		// private AnyNode anyNode; //
		// "09 90 28 10 29 ab ..." len = 20
		// <node1 id="1" name="3" password="111"/>

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public SexEnum getSex() {
			return sex;
		}

		public void setSex(SexEnum sex) {
			this.sex = sex;
		}

	}

	public static enum SexEnum implements EnumInteger {
		MALE(0), FEMALE(1);

		int value;

		private SexEnum(int value) {
			this.value = value;
		}

		@Override
		public int intValue() {
			return value;
		}
	}

	public static class Contanct extends SuperPojo {

		@Field(id = 1, type = NodeType.ATTR)
		private int id;

		@Field(id = 2)
		private String name;

		@Field(id = 3)
		private String telphone;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getTelphone() {
			return telphone;
		}

		public void setTelphone(String telphone) {
			this.telphone = telphone;
		}

	}
}
