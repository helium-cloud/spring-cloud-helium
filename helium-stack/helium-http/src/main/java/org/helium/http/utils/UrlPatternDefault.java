package org.helium.http.utils;

/**
 * Created by Coral on 1/12/17.
 */
public class UrlPatternDefault extends UrlPattern {
	public UrlPatternDefault() {
		super(UrlPatternType.DEFAULT, "/");
	}
	
	@Override
	public int match(String url) {
		return 1;
	}
}
