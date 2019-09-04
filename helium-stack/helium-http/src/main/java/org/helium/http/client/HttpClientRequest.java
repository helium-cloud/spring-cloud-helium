package org.helium.http.client;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpVersion;

import java.nio.charset.Charset;

public class HttpClientRequest extends DefaultHttpRequest {


	HttpClientRequest(HttpVersion http11, HttpMethod method,
	                  String asciiString) {
		super(http11, method, asciiString);
		
	}
	private String remoteAddress = "localhost";
	private int remotePort = 90;
	
    public void setContent(String content) {
        ChannelBuffer buffer = ChannelBuffers.copiedBuffer(content, Charset.forName("utf-8"));
        this.setContent(buffer);
		this.headers().add("Content-Length", buffer.writerIndex());
    }

	public void setContent(byte[] content,int offset,int length){
	    ChannelBuffer buffer=ChannelBuffers.copiedBuffer(content, offset, length);
	    this.setContent(buffer);
		this.headers().add("Content-Length", length);
	}

	public String toString(){
	    StringBuilder sb = new StringBuilder();
	    sb.append(super.toString()).append("\n");

	    sb.append(getContent().toString(Charset.forName("utf-8")));
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
}
