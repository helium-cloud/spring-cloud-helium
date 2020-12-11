package org.helium.http.client;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.internal.StringUtil;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;



public class HttpClientResponse extends DefaultFullHttpResponse {

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
        
        sb.append(super.content().toString(Charset.forName("utf-8")));
        return sb.toString();
    }
}
