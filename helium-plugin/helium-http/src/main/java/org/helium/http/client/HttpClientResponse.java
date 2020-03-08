package org.helium.http.client;

import java.nio.charset.Charset;
import java.security.interfaces.RSAKey;
import java.util.List;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.util.internal.StringUtil;

public class HttpClientResponse extends DefaultHttpResponse{

    public HttpClientResponse(HttpVersion version, HttpResponseStatus status) {
        super(version, status);
        
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append("\n");
        
        List<Map.Entry<String, String>> list = this.headers().entries();

        for (Map.Entry<String, String> e: list) {
	    	sb.append(e.getKey());
	    	sb.append(": ");
	    	sb.append(e.getValue());
	    	sb.append(StringUtil.NEWLINE);
        }
        
        sb.append(super.getContent().toString(Charset.forName("utf-8")));
        return sb.toString();
    }
}
