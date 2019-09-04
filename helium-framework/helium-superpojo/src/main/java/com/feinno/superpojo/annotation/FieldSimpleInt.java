package com.feinno.superpojo.annotation;

/**
 * 一个简单的字段节点，通过type，可以设定当前节点是一个node还是一个attr
 * 
 * @author lvmingwei
 * 
 */
public @interface FieldSimpleInt {

	/**
	 * 节点名称
	 */
	String name();

	/**
	 * 节点内容
	 */
	int value();

	/**
	 * 节点类型，非必选字段，仅在XML序列化时有效，默认为node(既为当前节点的子集)<br>
	 * 1. 在PB序列化时无用<br>
	 * 2. 在XML序列化时用于选择将当前节点放置在Attribute上还是字节点上<br>
	 * 3. 在JSON序列化时无用<br>
	 * PS:选择Attrbute类型时需要谨慎，该字段如果挂着附属内容，会得到警告<br>
	 */
	NodeType type() default NodeType.NODE;

}
