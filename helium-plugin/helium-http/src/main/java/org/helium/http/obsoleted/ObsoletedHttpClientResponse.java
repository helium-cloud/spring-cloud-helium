package org.helium.http.obsoleted;//package org.helium.http.client;//package helium.http.client;
//
//import com.ning.http.client.Request;
//import com.ning.http.client.Response;
//
//import java.io.IOException;
//import java.io.InputStream;
//
///**
// * @author Will.jingmiao
// * @version 创建时间：2015年4月7日 类说明
// */
//public class HttpClientResponse {
//	private Request request;
//	private Response response;
//
//	public HttpClientResponse(Request request, Response response) {
//		this.request = request;
//		this.response = response;
//	}
//
//	public int getStatusCode() {
//		return response.getStatusCode();
//	}
//
//	public InputStream getResponseBodyAsStream() throws IOException {
//		return response.getResponseBodyAsStream();
//	}
//
//	public String getResponseBodyAsString() throws IOException {
//		return response.getResponseBody();
//	}
//
//	public byte[] getResponseBodyAsBytes() throws IOException {
//		return response.getResponseBodyAsBytes();
//	}
//
//}
