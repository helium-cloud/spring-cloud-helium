package com.feinno.superpojo.generator;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import freemarker.cache.TemplateLoader;

/**
 * 
 * <b>描述: </b>freemarker中使用字符串作为模板的{@link TemplateLoader}
 * <p>
 * <b>功能: </b>freemarker中使用字符串作为模板的{@link TemplateLoader}
 * <p>
 * <b>用法: </b>由代码生成器调用，外部无需关注
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public class StringTemplateLoader implements TemplateLoader {

	private static final String DEFAULT_TEMPLATE_KEY = "_default_template_key";
	private Map<String, String> templates = new HashMap<String, String>();

	public StringTemplateLoader(String defaultTemplate) {
		if (defaultTemplate != null && !defaultTemplate.equals("")) {
			templates.put(DEFAULT_TEMPLATE_KEY, defaultTemplate);
		}
	}

	public void AddTemplate(String name, String template) {
		if (name == null || template == null || name.equals("") || template.equals("")) {
			return;
		}
		if (!templates.containsKey(name)) {
			templates.put(name, template);
		}
	}

	public void closeTemplateSource(Object templateSource) throws IOException {

	}

	public Object findTemplateSource(String name) throws IOException {
		if (name == null || name.equals("")) {
			name = DEFAULT_TEMPLATE_KEY;
		}
		return templates.get(name);
	}

	public long getLastModified(Object templateSource) {
		return 0;
	}

	public Reader getReader(Object templateSource, String encoding) throws IOException {
		return new StringReader((String) templateSource);
	}

}
