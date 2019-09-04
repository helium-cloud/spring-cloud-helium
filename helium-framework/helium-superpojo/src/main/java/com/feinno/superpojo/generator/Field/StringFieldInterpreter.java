package com.feinno.superpojo.generator.Field;

import com.feinno.superpojo.generator.ProtoFieldType;

/**
 * <b>描述: </b>用于序列化组件，用于String类型字段的解释器，正常每一种类型都需要继承AbstractFieldInterpreter类，
 * 但String有一些特殊， 因为他与包装类型的序列化实现代码相同，因此这里直接从WrapsFieldInterpreter重继承过来了
 * <p>
 * <b>功能: </b>String类型字段的解释器,用于提供String类型如何序列化、反序列化以及获取序列化长度
 * <p>
 * <b>用法: </b>该类由序列化组件在遍历类中的字段时遇到String类型时调用,外部无需调用
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public class StringFieldInterpreter extends WrapsFieldInterpreter {

	/** 这个解释器一定会用到 */
	private static final StringFieldInterpreter INSTANCE = new StringFieldInterpreter(ProtoFieldType.STRING);

	/**
	 * 单例模式，获得当前对象
	 * 
	 * @return
	 */
	public static FieldInterpreter getInstance() {
		return INSTANCE;
	}

	private StringFieldInterpreter(ProtoFieldType protoFieldType) {
		super(protoFieldType);
	}

}
