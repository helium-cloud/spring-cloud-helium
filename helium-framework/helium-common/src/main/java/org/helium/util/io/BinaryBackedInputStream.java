/*
 * FAE, Feinno App Engine
 *  
 * Create by windcraft Aug 16, 2011
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * 将ByteBffer转换成InputStream
 * 
 * Created by Coral
 */
public class BinaryBackedInputStream extends InputStream
{
	private ByteBuffer buf;

	public BinaryBackedInputStream(ByteBuffer buf)
	{
		this.buf = buf;
	}

	public synchronized int read() throws IOException
	{
		if (!buf.hasRemaining()) {
			return -1;
		}
		return buf.get() & 0xFF;
	}

	public synchronized int read(byte[] bytes, int off, int len) throws IOException
	{
		if (buf.remaining() == 0) {
			return -1; 
		} 
		
		len = Math.min(len, buf.remaining());
		buf.get(bytes, off, len);
		return len;
	}
}
