package test.org.helium.superpojo.bean;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Entity;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

import java.util.List;

/**
 * <test id="1">Feinno</test>
 * 
 * <test> <test1><id>1</id></test1> <test2><id>2</id></test2> </test>
 * 
 * @author Lv.Mingwei
 * 
 */
@Entity(name = "test")
public class TestBean extends SuperPojo {

	@Field(id = 1, type = NodeType.ATTR)
	private String id;

	@Field(id = 2)
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static void main(String args[]) {
		// CodeGenerator.setDebug(true);
		// CodeGenerator.setSourcePath("/tmp/java");
		// TestBean bean = new TestBean();
		// bean.setId("1");
		// bean.setName("Feinno");
		// System.out.println(new String(bean.toXmlByteArray()));
		//
		// String xml =
		// "<test id=\"1\"><name>aaa</name><T2><T21><T211>bbb211</T211><T212>bbb212</T212></T21></T2></test>";
		// System.out.println(xml);
		// TestBean result = new TestBean();
		// result.parseXmlFrom(xml);
		// System.out.println(result);
		// AnyNode anyNode = SuperPojoUtils.getAnyNode(result);
		// System.out.println(new String(anyNode.toXmlByteArray()));

//		FileUtil.write(ClassTemplate.PROTO_BUILDER_TEMPLATE, "/tmp/protobuilder");


		byte[] buffer = new byte[] { 0x17, 0x14, 0x6E, 0x6E, 0x54, 0x53, 0x43, 0x31, 0x33, 0x30,
				0x20, 0x28, 0x42, 0x75, 0x69, 0x6C, 0x64, 0x20, 0x32, 0x37, 0x35, 0x29, 0x12, 0x20, 0x30, 0x36, 0x2F,
				0x31, 0x30, 0x2F, 0x31, 0x35, 0x20, 0x31, 0x31, 0x3A, 0x34, 0x38, 0x3A, 0x32, 0x34, 0x0F, 0x4E, 0x6F,
				0x6E, 0x65, 0x43, 0x6F, 0x6E, 0x66, 0x69, 0x67, 0x75, 0x72, 0x65, 0x64, 0x21 };
		Bean bean = new Bean();
		bean.parsePbFrom(buffer);
		System.out.println(bean);

	}


	public static class Bean extends SuperPojo {

		@Field(id = 2)
		public List<String> list;

		public List<String> getList() {
			return list;
		}

		public void setList(List<String> list) {
			this.list = list;
		}

	}

}
