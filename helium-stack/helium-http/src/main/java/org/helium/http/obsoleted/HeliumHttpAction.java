//package org.helium.http.server;
//
//import SetterNode;
//import StringTemplateLoader;
//import SuperPojoUtils;
//import freemarker.template.ConfigProviderImpl;
//import freemarker.template.Template;
//import freemarker.template.TemplateException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//package helium.http;
//
///**
// * Action通用基类
// *
// * @author Lv.Mingwei
// *
// */
//public abstract class HeliumHttpAction {
//
//	private Action action;
//
//	private Map<String, String[]> parameters;
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(HeliumHttpAction.class);
//
//	public void service(Action action, HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//		try {
//
//			// Step 1. Mapping to field
//			this.action = action;
//			mappingToField(request);
//
//			// Step 2. ServiceInterface process
//			String result = this.service();
//
//			// STep 3. Render to html
//			String template = this.getTemplate(result);
//			Map<String, Object> parameters = getParameter();
//			render(response, template, parameters);
//		} catch (Exception e) {
//			LOGGER.error("Process http request failed.", e);
//		}
//	}
//
//	protected abstract String service();
//
//	/**
//	 * 映射请求中的Parameter到对象上
//	 *
//	 * @param request
//	 */
//	@SuppressWarnings("unchecked")
//	private void mappingToField(HttpServletRequest request) {
//		this.parameters = request.getParameterMap();
//		List<SetterNode> setters = new ArrayList<SetterNode>();
//		for (Entry<String, String[]> entry : parameters.entrySet()) {
//			if (Injector.getField(this, entry.getKey()) != null) {
//				SetterNode setterNode = new SetterNode();
//				setterNode.setField(entry.getKey());
//				SuperPojoUtils.setStringAnyNode(setterNode, entry.getValue()[0]);
//				setters.add(setterNode);
//			} else {
//				LOGGER.warn("Not Found [{}] Field in [{}].", entry.getKey(), this.getClass());
//			}
//		}
//		Injector.injectSetters(this, setters);
//	}
//
//	/**
//	 * 根据当前对象进行渲染
//	 *
//	 * @param template
//	 *            模板
//	 */
//	private void render(HttpServletResponse response, String templateString, Map<String, Object> parameters)
//			throws IOException, TemplateException, IllegalAccessException {
//		ConfigProviderImpl cfg = new ConfigProviderImpl();
//		cfg.setTemplateLoader(new StringTemplateLoader(templateString));
//		cfg.setDefaultEncoding("UTF-8");
//		Template template = cfg.getTemplate("");
//		response.setStatus(200);
//		response.setCharacterEncoding("UTF-8");
//		template.process(parameters, response.getWriter());
//	}
//
//	/**
//	 *
//	 * @param resultName
//	 * @return
//	 */
//	private String getTemplate(String resultName) throws FileNotFoundException, IOException {
//		Result result = action.getResult(resultName);
//		InputStream input = ConfigProviderImpl.getConfigAsStream(result.getFile(), this.getClass().getClassLoader());
//		byte[] buffer = new byte[input.available()];
//		input.read(buffer);
//		return new String(buffer);
//	}
//
//	/**
//	 *
//	 * @return
//	 * @throws IllegalAccessException
//	 */
//	private Map<String, Object> getParameter() throws IllegalAccessException {
//		Map<String, Object> retval = new HashMap<String, Object>();
//		java.lang.reflect.Field[] fields = this.getClass().getDeclaredFields();
//		for (java.lang.reflect.Field field : fields) {
//			field.setAccessible(true);
//			retval.put(field.getName(), field.get(this));
//			field.setAccessible(false);
//		}
//		return retval;
//	}
//
//	public String getParameter(String key) {
//		if (parameters.containsKey(key)) {
//			return parameters.get(key)[0];
//		} else {
//			return null;
//		}
//	}
//}
