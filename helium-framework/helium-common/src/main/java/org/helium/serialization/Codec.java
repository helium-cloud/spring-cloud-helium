/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2011-1-17
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <b>描述: </b>序列化的方式有多种，例如Java原生、json、protobuf，为了将多种序列化的方式与具体的使用解耦合而出现的此类，
 * <p>
 * <b>功能: </b>将多种序列化的方式与具体的业务使用解耦合
 * <p>
 * <b>用法: </b>
 *
 * <pre>
 * 获得一个类的序列化编解码器，并对这个类进行或反序列化
 * Codec codec = Serializer.getCodec(RpcRequestHeader.class);
 *
 * 序列化
 * RpcRequestHeader rpcRequestHeader = ...
 * byte[] buffer = codec.encode(rpcRequestHeader);
 *
 * 反序列化
 * rpcRequestHeader = codec.decode(buffer);
 * </pre>
 *
 * 关于序列化的详情请参见{@link Serializer}
 * <p>
 *
 * Created by Coral
 *
 */
public interface Codec {
	/**
	 *
	 * 将一个对象写入到输出流中,序列化
	 *
	 * @param obj
	 *            待编码对象
	 * @param stream
	 *            输出流
	 * @throws IOException
	 *             编码过程中有可能出现{@link IOException}异常
	 */
	void encode(Object obj, OutputStream stream) throws IOException;

	/**
	 * 将一个对象转为byte数组
	 *
	 * @param obj
	 *            待编码对象
	 * @return
	 * @throws IOException
	 *             编码过程中有可能出现{@link IOException}异常
	 */
	byte[] encode(Object obj) throws IOException;

	/**
	 * 从输入流读取指定长度的字节，且对这些字节进行解码，将解码后得到的结果返回
	 *
	 * @param stream
	 *            输入流
	 * @param length
	 *            读取长度
	 * @return
	 * @throws IOException
	 *             读取过程中有可能出现{@link IOException}异常
	 */
	<E> E decode(InputStream stream, int length) throws IOException;

	/**
	 * 对字节数组中的内容进行解码，并将解码后得到的结果返回
	 *
	 * @param buffer
	 *            待解码的字节数组
	 * @return
	 * @throws IOException
	 */
	<E> E decode(byte[] buffer) throws IOException;
}
