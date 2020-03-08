package org.helium.http.utils;

/**
 * Created by Gao Lei on 1/12/17.
 */
public class UrlPatternExtension extends UrlPattern {
	private String ext;
	
	public UrlPatternExtension(String text) {
		super(UrlPatternType.EXTENSION, text);
		ext = text.substring(1);
	}

	@Override
	public int match(String url) {
		if (url.endsWith(ext)) {
			return url.length();
		} else {
			return 0;
		}
	}
}
