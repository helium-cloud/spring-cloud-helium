package com.feinno.superpojo;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 用于Xml的序列及反序列接口
 * 
 * @author lvmingwei
 * 
 */
public interface IXmlPojo {

	void writeXmlTo(OutputStream output);

	byte[] toXmlByteArray();

	void parseXmlFrom(InputStream input);

	void parseXmlFrom(String xml);

}
