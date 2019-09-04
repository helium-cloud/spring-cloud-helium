package com.feinno.superpojo.type;


import com.feinno.superpojo.util.EnumParser;

/**
 * 
 * <b>描述: </b>用于描述一个类具有一个int类型的值，与枚举很相似，一个类或枚举实现了此接口后，这个类或枚举就可以{@link Flags}
 * 化，当用在枚举时，使这样枚举类型可以按照protobuf格式进行序列化， 且同样能够flags化的Enum
 * <p>
 * <b>功能: </b>使一个枚举类型可以进行protobuf序列化或flags化，或使一个类可以flags化
 * <p>
 * <b>用法: </b>
 * 
 * <pre>
 * public enum EnumIntegerDemo implements EnumInteger {
 * 	SSIP(0), MAP(1), SAP(2), CS(3), PRS(4);
 * 	int value;
 * 
 * 	EventSourceType(int value) {
 * 		this.value = value;
 * 	}
 * 
 * 	public int intValue() {
 * 		return value;
 * 	}
 * }
 * </pre>
 * <p>
 * 
 * @author 高磊 gaolei@feinno.com
 * @see ProtoEntity
 * @see ProtoManager
 * @see Serializer
 * @see EnumParser
 * @see Flags
 */
public interface EnumInteger {
	public int intValue();
}
