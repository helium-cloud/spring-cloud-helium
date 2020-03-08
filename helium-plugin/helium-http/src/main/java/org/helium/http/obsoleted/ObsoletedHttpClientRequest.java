package org.helium.http.obsoleted;//package org.helium.http.client;
//
//
//import com.ning.http.client.Request;
//import com.ning.http.client.RequestBuilder;
//import org.glassfish.grizzly.http.Method;
//
//import java.io.InputStream;
//
///**
// * @author Will.jingmiao
// * @version 创建时间：2014年10月22日 类说明
// */
//public class HttpClientRequest {
//
//	private RequestBuilder requestBuilder;
//
//	private HttpClientRequest(RequestBuilder requestBuilder) {
//		this.requestBuilder = requestBuilder;
//	}
//
//	public static HttpClientRequest newInstance(String url, Method method) {
//		HttpClientRequest request = new HttpClientRequest(new RequestBuilder(method.toString()).setUrl(url));
//
//		return request;
//	}
//
//	public Request getOriginalRequest() {
//		return requestBuilder.build();
//	}
//
//	public void addParameter(String key, String value) {
//		requestBuilder.addParameter(key, value);
//	}
//
//	public void addHeader(String key, String value) {
//		requestBuilder.addHeader(key, value);
//	}
//
//	public void setBody(byte x0[]) {
//		requestBuilder.setBody(x0);
//	}
//
//	public void setBody(InputStream x0) {
//		requestBuilder.setBody(x0);
//	}
//
//	public void setBody(String x0) {
//		requestBuilder.setBody(x0);
//	}
//}
