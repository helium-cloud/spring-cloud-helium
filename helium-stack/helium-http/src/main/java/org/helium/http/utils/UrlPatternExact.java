package org.helium.http.utils;

/**
 * Created by Coral on 1/12/17.
 */
public class UrlPatternExact extends UrlPattern {
	public UrlPatternExact(String s) {
		super(UrlPatternType.EXACT, s);
	}
	
	@Override
	public int match(String url) {
		if (getText().equals(url)) {
			return getText().length();
		} else {
			return 0;
		}
	}
}
