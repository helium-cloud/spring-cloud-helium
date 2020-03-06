package org.helium.framework.route.abtest;

import org.helium.framework.module.ModuleContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Coral on 8/19/15.
 */
public class RegexFactorImpl implements Factor {
	private String key;
	private String regex;
	private Pattern pattern;

	public RegexFactorImpl(String key, String regex) {
		this.key = key;
		this.regex = regex;
		pattern = Pattern.compile(regex);
	}

	@Override
	public boolean apply(ModuleContext ctx) {
		Object v = ctx.getModuleData(key);
		if (v == null) {
			return false;
		}
		Matcher matcher = pattern.matcher(v.toString());
		return matcher.matches();
	}

	@Override
	public String toString() {
		return key + " regex " + regex;
	}
}
