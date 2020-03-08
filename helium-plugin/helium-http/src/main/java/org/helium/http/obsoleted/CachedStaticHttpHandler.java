package org.helium.http.obsoleted;//package org.helium.http.server;
//
//import org.helium.util.StringUtils;
//import org.glassfish.grizzly.http.servlet.HttpHandler;
//import org.glassfish.grizzly.http.servlet.Request;
//import org.glassfish.grizzly.http.servlet.Response;
//import org.jboss.netty.handler.codec.http.HttpRequest;
//import org.jboss.netty.handler.codec.http.HttpResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.net.URL;
//import java.util.Hashtable;
//import java.util.Map;
//
///**
// * Created by Lei Gao on 7/2/15.
// * 使用resources目录作为静态页面负载的http服务器处理类
// *
// *
// * @author 高磊 gaolei@feinno.com
// */
//public class CachedStaticHttpHandler extends HttpHandler {
//	CachedStaticHttpHandler() {
//
//	}
//
//	@Override
//	public void service(Request request, Response response) throws Exception {
//	}
//
//	private static class Entry
//	{
//		private int statusCode;
//		private byte[] buffer;
//	}
//
//	private String requestRoot;
//	private String resourceRoot;
//
//	@Override
//	public void handle(HttpRequest request, HttpAsyncExchange httpExchange, HttpContext context) throws HttpException, IOException
//	{
//		LOGGER.info("http request received {}", request);
//		String rpath = httpExchange.getRequest().getRequestLine().getUri().substring(requestRoot.length());
//		if(rpath.indexOf("?")>-1)
//		{
//			rpath = rpath.substring(0,rpath.indexOf("?"));
//		}
//		if (StringUtils.isNullOrEmpty(rpath))
//		{
//			if (!"".equals(defaultContent))
//			{
//				String redirect = httpExchange.getRequest().getRequestLine().getUri() + defaultContent;
//
//				HttpResponse response = httpExchange.getResponse();
//				response.setStatusCode(302);
//				response.setHeader("Location", redirect);
//				httpExchange.submitResponse(new BasicAsyncResponseProducer(response));
//
//				LOGGER.info("redirect to {}", redirect);
//				return;
//			}
//		}
//
//		Entry entry;
//		synchronized (syncRoot)
//		{
//			entry = entrys.get(rpath);
//			if (entry == null)
//			{
//				entry = loadEntry(rpath);
//				entrys.put(rpath, entry);
//			}
//		}
//		if (entry.statusCode != 200)
//		{
//			HttpResponse response = httpExchange.getResponse();
//			response.setStatusCode(entry.statusCode);
//			httpExchange.submitResponse(new BasicAsyncResponseProducer(response));
//		}
//		else
//		{
//			HttpResponse response = httpExchange.getResponse();
//			response.setStatusCode(200);
//			ByteArrayEntity entity = new ByteArrayEntity(entry.buffer);
//			response.setEntity(entity);
//			httpExchange.submitResponse(new BasicAsyncResponseProducer(response));
//		}
//		LOGGER.info("request {} get {}", httpExchange.getRequest().getRequestLine().getUri(), entry.statusCode);
//	}
//
//	private String defaultContent;
//	private Class<?> rootClazz;
//	private Object syncRoot;
//	private Map<String, Entry> entrys;
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(NCachedStaticResourceHandler.class);
//
//	public NCachedStaticResourceHandler(String requestRoot, String resourceRoot, String defaultContent, Class<?> clazz)
//	{
//		this.requestRoot = requestRoot;
//		this.resourceRoot = resourceRoot;
//		if (!this.resourceRoot.endsWith("/"))
//		{
//			this.resourceRoot = this.resourceRoot + "/";
//		}
//		if (!this.resourceRoot.startsWith("/"))
//		{
//			this.resourceRoot = "/" + this.resourceRoot;
//		}
//		this.rootClazz = clazz;
//		syncRoot = new Object();
//		entrys = new Hashtable<String, Entry>();
//
//		if (defaultContent != null)
//		{
//			this.defaultContent = defaultContent;
//		}
//		else
//		{
//			this.defaultContent = "";
//		}
//	}
//
//	private Entry loadEntry(String rpath)
//	{
//		Entry etr;
//		String path = resourceRoot + rpath;
//		URL fileUrl = rootClazz.getResource(path);
//
//		if (fileUrl == null)
//		{
//			etr = new Entry();
//			etr.statusCode = 404;
//			LOGGER.info("resource {} not found", path);
//			return etr;
//		}
//		else
//		{
//			try
//			{
//				InputStream in = rootClazz.getResourceAsStream(path);
//				ByteArrayOutputStream out = new ByteArrayOutputStream();
//				byte[] buffer = new byte[4096];
//				while (true)
//				{
//					int len = in.read(buffer, 0, buffer.length);
//					if (len > 0)
//					{
//						out.write(buffer, 0, len);
//					}
//					else
//					{
//						break;
//					}
//				}
//				etr = new Entry();
//				etr.statusCode = 200;
//				etr.buffer = out.toByteArray();
//				LOGGER.info("load {} into cache", rpath);
//				return etr;
//			}
//			catch (IOException ex)
//			{
//				LOGGER.error("load stream failed {}:" + rpath, ex);
//				etr = new Entry();
//				etr.statusCode = 500;
//				return etr;
//			}
//		}
//	}
//}
//
