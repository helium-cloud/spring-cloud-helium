package org.helium.http.test;

import org.helium.framework.annotations.FieldSetter;
import org.helium.framework.annotations.ServletImplementation;
import org.helium.http.servlet.HttpMappings;
import org.helium.http.servlet.extension.TemplateHtml;
import org.helium.http.servlet.extension.TemplateServlet;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Coral on 7/28/15.
 */
@ServletImplementation(id = "sample:TestTemplateServlet")
@HttpMappings(contextPath = "/sample", urlPattern = "/template1")
@TemplateHtml("/webroot/template1.html")
public class TestTemplateServlet extends TemplateServlet {
	@FieldSetter("${HELLO}")
	private String hello;

//	@FieldSetter("URCS_UPDB")
//	private Database db;
//
//	@FieldSetter("URCS_GRPRD")
//	private RedisClient redis;

	@Override
	public Map<String, String> getValues() {
		Map<String, String> maps = new HashMap<>();
		maps.put("hello", hello);
		maps.put("good", "sonnawabitch");
		return maps;
	}
}
