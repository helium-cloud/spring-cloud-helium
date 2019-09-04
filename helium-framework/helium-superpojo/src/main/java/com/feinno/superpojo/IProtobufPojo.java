package com.feinno.superpojo;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 用于Protobuf的序列及反序列化接口
 * 
 * @author lvmingwei
 * 
 */
public interface IProtobufPojo {

	public void writePbTo(OutputStream output);
	
	public byte[] toPbByteArray();

	public void parsePbFrom(InputStream input);

	public void parsePbFrom(byte[] buffer);

}
