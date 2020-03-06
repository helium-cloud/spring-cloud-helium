/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2011-1-5
 *
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

import org.helium.serialization.Codec;
import org.helium.serialization.Serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;

/**
 * 保存Rpc包体的实体类 用途如下: <br>
 * 1. 将RequestBody序列化到传输层, 从value创建, 调用encode方法 <br>
 * 2. 将RequestBody从传输层解析出来, 从byte[]创建, 调用decode方法 <br>
 * 3. 将ResponseBody从序列化到传输层, 从value创建, 调用encode方法 <br>
 * 4. 将ResponseBody从传输从解析出来, 从byte[]创建, 调用decode方法 <br>
 * <p>
 * 另外RpcBody存在以下使用方式 <br>
 * 1. 不序列化情况下进行重新读取, 当Rpc转本机调用的时候 <br>
 * 2. 不反序列化情况下进行转发, 当Rpc进行PROXY路由的时候 <br>
 * <p>
 * 需要在设计期进行支持 <br>
 * <p>
 * 2012.04.19 gaolei:增加传入codec的encode与decode方法，可节省一次字典查询
 * <p>
 * Created by Coral
 */
public class RpcBody {
	public static final byte[] EMPTY_BUFFER = new byte[0];

	private boolean asError;
	private byte[] buffer;
	private Object value;
	private String codecName;

	public String getCodecName() {
		return codecName;
	}

	public void setCodecName(String codecName) {
		this.codecName = codecName;
	}

	public RpcBody(Object value) {
		this.asError = false;
		this.value = value;
		this.buffer = null;
	}

	public RpcBody(byte[] buffer) {
		this.buffer = buffer;
		this.value = null;
		this.asError = false;
	}

	public RpcBody(Object value, boolean asError, String codecName, boolean isBuffer) {
		assert value != null : "RpcBody's value can't be null";

		if (isBuffer) {
			this.asError = asError;
			this.value = null;
			this.buffer = (byte[]) value;
		} else {
			this.asError = asError;
			this.value = value;
			this.buffer = null;
		}

		setCodecName(codecName);
	}

	public RpcBody(Object value, boolean asError, String codecName) {
		this(value, asError);
		setCodecName(codecName);
	}

	public RpcBody(Object value, boolean asError) {
		assert value != null : "RpcBody's value can't be null";

		if (value instanceof byte[]) {
			byte[] buffer = (byte[]) value;
			this.asError = asError;
			this.value = null;
			this.buffer = buffer;
		} else {
			this.asError = asError;
			this.value = value;
			this.buffer = null;
		}
	}

	public void encode(OutputStream stream) throws IOException {
		//
		// 满足以下需求
		// * 1. 不序列化情况下进行读取,
		// * 序列化的Buffer不会缓存, 因为没有连续两次序列化的需求
		if (buffer != null) {
			stream.write(buffer);
		} else {
			if (asError) {
				Throwable e = (Throwable) value;
				PrintStream ps = new PrintStream(stream);
//				ps.println(String.format("Exception from [Computer: %s,Pid: %s, Service: %s]", ServiceEnviornment.getComputerName(),
//						ServiceEnviornment.getPid(), ServiceEnviornment.getServiceName()));
				e.printStackTrace(ps);
			} else {
				Serializer.encode(value, getCodecName(), stream);
			}
		}
	}

	public void encode(Codec codec, OutputStream stream) throws IOException {
		//
		// 满足以下需求
		// * 1. 不序列化情况下进行读取,
		// * 序列化的Buffer不会缓存, 因为没有连续两次序列化的需求
		if (buffer != null) {
			stream.write(buffer);
		} else {
			if (asError) {
				Throwable e = (Throwable) value;
				PrintStream ps = new PrintStream(stream);
//				ps.println(String.format("Exception from [Computer: %s,Pid: %s, Service: %s]", ServiceEnviornment.getComputerName(),
//						ServiceEnviornment.getPid(), ServiceEnviornment.getServiceName()));
				e.printStackTrace(ps);
			} else {
				codec.encode(value, stream);
			}
		}
	}

	public Object forceDecode(Class<?> clazz) throws IOException {
		if (!asError) {
			if (clazz == null) {
				value = null;
			} else {
				value = Void.class.equals(clazz) ? null : Serializer.decode(clazz, buffer);
			}
		} else {
			String text = decodeText();
			value = new Exception(text);
		}
		return value;
	}

	public Object forceDecode(Codec codec) throws IOException {
		if (!asError) {
			return codec.decode(buffer);
		} else {
			String text = decodeText();
			value = new Exception(text);
		}
		return value;
	}

	public Object decode(Class<?> clazz) throws IOException {
		//
		// 满足 1. 不序列化情况下进行读取,
		if (value != null) {
			return value;
		} else {
			if (!asError) {
				if (clazz == null) {
					value = null;
				} else {
					value = Void.class.equals(clazz) ? null : Serializer.decode(clazz, getCodecName(), buffer);
				}
			} else {
				String text = decodeText();
				value = new Exception(text);
			}
			return value;
		}
	}

	public Object decode(Codec codec) throws IOException {
		//
		// 满足 1. 不序列化情况下进行读取,
		if (value != null) {
			return value;
		} else {
			if (!asError) {
				return codec.decode(buffer);
			} else {
				String text = decodeText();
				value = new Exception(text);
			}
			return value;
		}
	}

	public String decodeText() {
		if (buffer == null) {
			return "";
		}

		try {
			return new String(buffer, 0, buffer.length, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "!!!Exception decode failed:" + e.toString();
		}
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public Object getValue() {
		return value;
	}

	public Throwable getError() {
		if (asError) {
			if (value == null) {
				value = new Exception(decodeText());
				((Throwable) value).setStackTrace(new StackTraceElement[0]);
			}
			return (Throwable) value;
		} else {
			return null;
		}
	}

	public Throwable getError(Class<? extends Throwable> errorClazz) {
		if (asError) {
			if (value == null) {
				try {
					Constructor<? extends Throwable> constructor = errorClazz.getConstructor(String.class);
					if (constructor == null) {
						value = errorClazz.newInstance();
					} else {
						value = constructor.newInstance(decodeText());
					}
				} catch (Exception e) {
					value = new Exception(decodeText());
				}
				((Throwable) value).setStackTrace(new StackTraceElement[0]);
			}
			return (Throwable) value;
		} else {
			return null;
		}
	}
}