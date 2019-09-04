package org.helium.http.test;

import org.helium.http.utils.UrlPattern;
import org.helium.http.utils.UrlPatternGroup;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Coral on 1/12/17.
 */
public class UrlPatternTest {
	@Test
	public void test1() throws Exception {
		UrlPattern pattern1 = UrlPattern.parse("/");
		UrlPattern pattern2 = UrlPattern.parse("/aab");
		UrlPattern pattern3 = UrlPattern.parse("*.do");
		UrlPattern pattern4 = UrlPattern.parse("/path/do/*");
		UrlPattern pattern5 = UrlPattern.parse("/path/do/help.do");
		UrlPattern pattern6 = UrlPattern.parse("/path/abc/*");
		UrlPattern pattern7 = UrlPattern.parse("/path/$UserId/abc/*");
		
		UrlPatternGroup group = new UrlPatternGroup();
		group.addPattern(pattern1);
		group.addPattern(pattern2);
		group.addPattern(pattern3);
		group.addPattern(pattern4);
		group.addPattern(pattern5);
		group.addPattern(pattern6);
		group.addPattern(pattern7);	
		
		assertPattern(group, "/sss", pattern1);
		assertPattern(group, "/aab", pattern2);
		assertPattern(group, "/abbccs.do", pattern3);
		assertPattern(group, "/path/do/1", pattern4);
		assertPattern(group, "/path/do/help.do", pattern5);
		assertPattern(group, "/path/abc/11", pattern6);
		assertPattern(group, "/path/113322/abc", pattern7);
	}
	
	private void assertPattern(UrlPatternGroup group, String url, UrlPattern expect) {
		UrlPattern pattern = group.match(url);
		Assert.assertEquals("url: "+ url, expect, pattern);
	}
}
