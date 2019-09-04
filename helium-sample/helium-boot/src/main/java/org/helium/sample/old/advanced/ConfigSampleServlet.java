//package org.helium.sample.old.advanced;
//
//import org.helium.framework.annotations.FieldSetter;
//import org.helium.framework.annotations.ServiceSetter;
//import org.helium.framework.annotations.ServletImplementation;
//import org.helium.framework.configuration.ConfigProvider;
//import org.helium.http.servlet.HttpMappings;
//import org.helium.http.servlet.HttpServlet;
//import org.helium.http.servlet.HttpServletContext;
//
///**
// * Created by Coral on 7/13/17.
// */
//@ServletImplementation(id = "quickstart:ConfigSampleServlet")
//@HttpMappings(contextPath = "/quickstart", urlPattern = "/config")
//public class ConfigSampleServlet extends HttpServlet {
//	@ServiceSetter
//	private ConfigProvider configProvider;
//
//	private SampleConfigXml configXml;
//
//	@FieldSetter("USER_NAME")
//	private String configValue;
//
//	public void initialize() {
//		configXml = configProvider.loadXml("biz/sample/sample-config.xml", SampleConfigXml.class);
//	}
//
//
//	@Override
//	public void process(HttpServletContext ctx) throws Exception {
//		try {
//			ctx.getResponse().getOutputStream().println("config:" + configXml.toXmlString());
//		} catch (Exception ex) {
//			ctx.sendError(ex);
//		}
//	}
//}
