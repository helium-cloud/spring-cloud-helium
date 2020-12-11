package org.helium.http.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import java.lang.reflect.Field;
import java.nio.charset.Charset;


public class HttpClientRequest extends DefaultFullHttpRequest {


	HttpClientRequest(HttpVersion http11, HttpMethod method,
					  String asciiString) {
		super(http11, method, asciiString);
		
	}
	private String remoteAddress = "localhost";
	private int remotePort = 90;
	
    public void setContent(String content) {
		ByteBuf buffer = Unpooled.copiedBuffer(content, Charset.forName("utf-8"));
		setByteBufContent(buffer);
		this.headers().add("Content-Length", buffer.writerIndex());
    }

	public void setContent(byte[] content,int offset,int length){
		ByteBuf buffer= Unpooled.copiedBuffer(content, offset, length);
		setByteBufContent(buffer);
		this.headers().add("Content-Length", length);
	}

	public String toString(){
	    StringBuilder sb = new StringBuilder();
	    sb.append(super.toString()).append("\n");

	    sb.append(content().toString(Charset.forName("utf-8")));
        if (sb.length() > 4000000) {
            sb.delete(1000, sb.length() - 1000);
            sb.insert(1000, "\r\n\r\ntoo long...\r\n\r\n");
        }
	    return sb.toString();
	}

	public void addHeader(final String name, final Object value) {
		this.headers().add(name, value);
	}

	public void setHeader(final String name, final Object value) {
		this.headers().set(name, value);
	}

	public void setHeader(final String name, final Iterable<?> values) {
		this.headers().set(name, values);
	}

	public void removeHeader(final String name) {
		this.headers().remove(name);
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	public int getRemotePort() {
		return remotePort;
	}

	private void setByteBufContent(ByteBuf byteBuf){
    	// 通过打破封装方式直接对值进行操作
		try {
			Field field = null;
			field = DefaultFullHttpRequest.class.getDeclaredField("content");
		// 打破封装
			field.setAccessible(true);
			field.set(this, byteBuf);
		} catch (Exception e) {
			System.out.println("setContent ERROR:" +  remoteAddress);
		}
	}
}
