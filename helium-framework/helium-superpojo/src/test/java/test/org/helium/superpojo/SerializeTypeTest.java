package test.org.helium.superpojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Entity;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.type.EnumInteger;

public class SerializeTypeTest {

	public static void main(String args[]) {
		String jsonStr = " {'name':'feinno'} ";
		String xmlStr = " <name>xxx</name> ";
		String pbStr = " { ' a ' : ' b ' }";
		ProtoBean protoBean = ProtoBean.newInstance(5);
		protoBean.toPbByteArray();
		protoBean = ProtoBean.newInstance(0);
		// protoBean.parsePbFrom(" < a > b < / a >".getBytes());
		protoBean.parsePbFrom(pbStr.getBytes());
		// protoBean = ProtoBean.newInstance(0);
		// protoBean = SuperPojoManager.parseJsonFrom(" { ' a ' : ' b ' }",
		// ProtoBean.class);

		System.out.println("jsonStr is \t" + parseSerializeType(jsonStr.getBytes()));
		System.out.println("xmlStr is \t" + parseSerializeType(xmlStr.getBytes()));
		System.out.println("protoBean is \t" + parseSerializeType(pbStr.getBytes()));

		System.out.println(Arrays.toString(pbStr.getBytes()));
		System.out.println(protoBean);
	}

	public static SerializeType parseSerializeType(byte[] buffer) {
		boolean isPreviousSpace = false;
		for (int i = 0; i < buffer.length; i++) {
			byte b = buffer[i];
			SerializeFlag flag = SerializeFlag.valueOfByStartFlag(b);
			switch (flag) {
			case LEFT_BRACE:
			case LEFT_BRACKET:
				if (isPreviousSpace) {
					// 如果上一个是空格，那么当前是"{",按照Json规则，"后面"里不允许有空格
					// 综上所述，当下一个不是空格时，则当前是PB类型
					if (SerializeFlag.SPACE == SerializeFlag.valueOfByStartFlag(buffer[i + 1])) {
						
					} else {
						return flag.getSerializeType();
					}
				} else {
					return flag.getSerializeType();
				}
			case LESS_THAN:
				if (isPreviousSpace) {
					// 如果上一个是空格，那么当前是"<",按照XML规则，<>里不允许有空格
					// 综上所述，当下一个不是空格时，则当前是PB类型
					if (SerializeFlag.SPACE == SerializeFlag.valueOfByStartFlag(buffer[i + 1])) {
						return SerializeFlag.OTHER.getSerializeType();
					} else {
						return flag.getSerializeType();
					}
				} else {
					return flag.getSerializeType();
				}
			case OTHER:
				return flag.getSerializeType();
			case SPACE:
				isPreviousSpace = true;
				break;
			default:
				break;
			}
		}

		return SerializeType.PROTOBUFFER;
	}

	public static enum SerializeType implements EnumInteger {
		UNKNOW(0), PROTOBUFFER(1), JSON(2), XML(3);
		int value;

		SerializeType(int value) {
			this.value = value;
		}

		public int intValue() {
			return value;
		}
	}

	public static enum SerializeFlag implements EnumInteger {

		SPACE(" ", " ", SerializeType.UNKNOW), LEFT_BRACE("{", "}", SerializeType.JSON), LEFT_BRACKET("[", "]",
				SerializeType.JSON), LESS_THAN("<", ">", SerializeType.XML), OTHER("Other", "Other",
				SerializeType.PROTOBUFFER);

		private byte startFlag;
		private byte endFlag;
		private SerializeType type;

		SerializeFlag(String start, String end, SerializeType type) {
			this.startFlag = start.getBytes()[0];
			this.endFlag = end.getBytes()[0];
			this.type = type;
		}

		public int intValue() {
			return startFlag;
		}

		public byte startFlag() {
			return startFlag;
		}

		public byte endFlag() {
			return endFlag;
		}

		public SerializeType getSerializeType() {
			return type;
		}

		public static SerializeFlag valueOfByStartFlag(byte b) {
			for (SerializeFlag flag : SerializeFlag.values()) {
				if (flag.startFlag() == b) {
					return flag;
				}
			}
			return SerializeFlag.OTHER;
		}
	}

	@Entity(name = "a")
	public static class ProtoBean extends SuperPojo {

		@Field(id = 4)
		private List<Integer> userIds;

		public List<Integer> getUserIds() {
			return userIds;
		}

		public void setUserIds(List<Integer> userIds) {
			this.userIds = userIds;
		}

		public static ProtoBean newInstance(int number) {
			ProtoBean bean = new ProtoBean();
			List<Integer> userIds = new ArrayList<Integer>();
			for (int i = 1; i < number; i++) {
				userIds.add(60);
			}
			bean.setUserIds(userIds);
			return bean;
		}

	}
}
