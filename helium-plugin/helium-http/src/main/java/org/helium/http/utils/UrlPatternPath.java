package org.helium.http.utils;

import com.feinno.superpojo.util.StringUtils;
import org.helium.util.Consumer2;
import org.helium.util.StringParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gao Lei on 1/12/17.
 */
public class UrlPatternPath extends UrlPattern {
	private List<String> sections = new ArrayList<>();
	
	public UrlPatternPath(String text) {
		super(UrlPatternType.PATH, text);
		List<String> list = splitUrl(text);
		
		for (int i = 0; i < list.size(); i++) {
			sections.add(list.get(i));
		}
	}

	@Override
	public int match(String url) {
		return matchPath(url, null);
	}
	
	public int matchPath(String url, Consumer2<String, String> parameterizedFunc) {
		if (sections.size() == 0) {
			return 1;
		}
		List<String> rlist = splitUrl(url);
		int i = 0;
		int matchLength = 1;
		while (i < sections.size() && i < rlist.size()) {
			String lval = sections.get(i);
			String rval = rlist.get(i);

			if (lval.startsWith("$")) {
				matchLength += rval.length() + 1;
				if (parameterizedFunc != null) {
					parameterizedFunc.apply(lval, rval);
				}
			} else if (lval.equals(rval)) {
				matchLength += rval.length() + 1;
			} else {
				return 0;
			}
			i++;
		}
		return matchLength;	
	}
	
	
	private static List<String> splitUrl(String url) {
		List<String> list = new ArrayList<>();
		StringParser parser = new StringParser(url);
		parser.consume(1);
		while (true) {
			String section = parser.getToken("/");
			if (StringUtils.isNullOrEmpty(section)) {
				return list;
			}
			if ("*".equals(section)) {
				return list;
			}
			list.add(section);
		}
	}
}
