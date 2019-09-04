/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2011-8-4
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.util.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * I/O流帮助类
 * 
 * Created by Coral
 */
public class StreamHelper
{
	/**
	 * 
	 * 读一个流，读到想要的字节数
	 * @param in
	 * @param buffer
	 * @param offset
	 * @param count
	 * @throws IOException
	 */
	public static void safeRead(InputStream in, byte[] buffer, int offset, int count) throws IOException
	{
		while (count > 0) {
			int readed = in.read(buffer, offset, count);
			if (readed < 0)
				throw new EOFException();
			offset += readed;
			count -= readed; 
		}
	}
}
