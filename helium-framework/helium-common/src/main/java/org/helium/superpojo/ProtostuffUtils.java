package org.helium.superpojo;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * @author wuhao
 * @description: ObjectUtils
 * @createTime 2021/07/07 23:51:00
 */

public class ProtostuffUtils {
	/**
	 * 转化为字节流
	 *
	 * @param object
	 * @return
	 */
	public static byte[] toBytes(Object object) {
		LinkedBuffer buffer = LinkedBuffer.allocate();
		Schema schema = RuntimeSchema.getSchema(object.getClass());
		byte[] bytes = ProtobufIOUtil.toByteArray(object, schema, buffer);
		return bytes;
	}

	public static <T> T toObject(byte[] bytes, Class<T> tClass) {
		LinkedBuffer buffer = LinkedBuffer.allocate();
		Schema schema = RuntimeSchema.getSchema(tClass);
		T object = (T) schema.newMessage();
		ProtobufIOUtil.mergeFrom(bytes, object, schema);
		return object;
	}
}
