package org.helium.http.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Gao Lei on 1/12/17.
 */
public class UrlPatternGroup {
	private UrlPattern defaultPattern;
	private Map<String, UrlPattern> extensionPatterns = new HashMap<>();
	private Map<String, UrlPattern> exactPatterns = new HashMap<>();
	private List<UrlPattern> pathPatterns = new ArrayList<>();

	public UrlPatternGroup() {
	}

	/**
	 * 为了保证效率, add操作使用copy-and-modify模式
	 * @return
	 */
	public UrlPatternGroup deepClone() {
		UrlPatternGroup r = new UrlPatternGroup();
		
		r.defaultPattern = this.defaultPattern;
		this.exactPatterns.forEach((k, v) -> {
			r.exactPatterns.put(k, v);
		});
		this.extensionPatterns.forEach((k, v) -> {
			r.extensionPatterns.put(k, v);
		});
		this.pathPatterns.forEach(v -> r.pathPatterns.add(v));
		return r;
	}

	public void addPattern(UrlPattern pattern) {
		switch (pattern.getType()) {
			case DEFAULT:
				if (defaultPattern != null) {
					throw new IllegalArgumentException("default urlPattern '/' duplicated");
				} else {
					defaultPattern = pattern;
				}
				break;
			case EXACT:
				exactPatterns.put(pattern.getText(), pattern);
				break;
			case PATH:
				pathPatterns.add(pattern);
				break;
			case EXTENSION:
				extensionPatterns.put(pattern.getText(), pattern);
				break;
			default:
				throw new IllegalArgumentException("WTF!!" + pattern);
		}
	}

	/**
	 * 匹配顺序
	 * 1. 精确匹配
	 * 2. 路径匹配，先最长路径匹配，再最短路径匹配
	 * 3. 扩展名匹配
	 * 4. 缺省匹配，以上都找不到servlet，就用默认的servlet
	 * @param url
	 * @return
	 */
	public UrlPattern match(String url) {
		UrlPattern r;
		r = exactPatterns.get(url);
		if (r != null) {
			return r;
		}
		
		//
		// /$1/sims/aa?adfadf
		// /*
		// /$2
		int maxLength = 0;
		for (UrlPattern pattern: pathPatterns) {
			 int len = ((UrlPatternPath)pattern).match(url);
			 if (len > maxLength) {
			 	maxLength = len;
			 	r = pattern;
			 }
		}
		
		if (r != null) {
			return r;	
		}

		for (UrlPattern pattern: extensionPatterns.values()) {
			if (pattern.match(url) > 0) {
				return pattern;
			}
		}
		
		if (defaultPattern != null) {
			return defaultPattern;
		} else {
			return null;
		}
	}
}
