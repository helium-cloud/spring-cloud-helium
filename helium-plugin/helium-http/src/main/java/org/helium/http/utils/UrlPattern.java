package org.helium.http.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个类用于扩展和解析url-pattern匹配规范的以下方式
 * 
 * 1. 精确匹配
 * 2. 扩展名匹配
 * 3. 路径匹配
 * 4. 默认匹配
 * 5. 带通配符的Restful风格路径匹配 {helium框架扩展}
 *
 * Created by Gao Lei on 1/12/17.
 */
public abstract class UrlPattern {
	private UrlPatternType type;
	private String text;
	private Object attactment;
	
	public UrlPattern(UrlPatternType type, String text) {
		this.type = type;
		this.text = text;
	}
	
	public UrlPatternType getType() {
		return type;
	}
	
	public String getText() {
		return text;
	}

	public Object getAttactment() {
		return attactment;
	}

	public void setAttactment(Object attactment) {
		this.attactment = attactment;
	}

	private List<String> sections = new ArrayList<>();

	private void addSection(String section) {
		sections.add(section);
	}

	@Override
	public String toString() {
		return "UrlPattern:" + text;
	}

	/**
	 * 返回能够匹配的url长度, 参数是不参与匹配的, 0表示无法匹配
	 * @param url
	 * @return
	 */
	public abstract int match(String url);
	
	public static UrlPattern parse(String str) {
		if ("/".equals(str)) {
			return new UrlPatternDefault();
		}
		// 2017/10/26 更改, 之前为    *.
		if (str.startsWith("*")) {
			return new UrlPatternExtension(str);
		}
		if (!str.startsWith("/")) {
			throw new IllegalArgumentException("urlPattern must start with /");
		}
		if (!str.endsWith("*")) {
			return new UrlPatternExact(str);
		}
		return new UrlPatternPath(str);
	}
}
