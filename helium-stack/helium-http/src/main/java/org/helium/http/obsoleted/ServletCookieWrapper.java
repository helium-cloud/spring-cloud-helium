//package org.helium.http.servlet.extension.spi;
//
//import org.glassfish.grizzly.http.Cookie;
//
///**
// * @author coral
// * @version 创建时间：2014年9月24日 类说明
// */
//class ServletCookieWrapper extends Cookie {
//
//	public ServletCookieWrapper(String name, String value) {
//		super(name, value);
//	}
//
//	private javax.servlet.http.Cookie wrappedCookie = null;
//
//	@Override
//	public void setComment(String purpose) {
//		wrappedCookie.setComment(purpose);
//	}
//
//	@Override
//	public String getComment() {
//		return wrappedCookie.getComment();
//	}
//
//	@Override
//	public void setDomain(String pattern) {
//		wrappedCookie.setDomain(pattern);
//	}
//
//	@Override
//	public String getDomain() {
//		return wrappedCookie.getDomain();
//	}
//
//	@Override
//	public void setMaxAge(int expiry) {
//		wrappedCookie.setMaxAge(expiry);
//	}
//
//	@Override
//	public int getMaxAge() {
//		return wrappedCookie.getMaxAge();
//	}
//
//	@Override
//	public void setPath(String uri) {
//		wrappedCookie.setPath(uri);
//	}
//
//	@Override
//	public String getPath() {
//		return wrappedCookie.getPath();
//	}
//
//	@Override
//	public void setSecure(boolean flag) {
//		wrappedCookie.setSecure(flag);
//	}
//
//	@Override
//	public boolean isSecure() {
//		return wrappedCookie.getSecure();
//	}
//
//	@Override
//	public String getName() {
//		return wrappedCookie.getName();
//	}
//
//	@Override
//	public void setValue(String newValue) {
//		wrappedCookie.setValue(newValue);
//	}
//
//	@Override
//	public String getValue() {
//		return wrappedCookie.getValue();
//	}
//
//	@Override
//	public int getVersion() {
//		return wrappedCookie.getVersion();
//	}
//
//	@Override
//	public void setVersion(int v) {
//		wrappedCookie.setVersion(v);
//	}
//
//	public Object cloneCookie() {
//		return wrappedCookie.clone();
//	}
//
//	public javax.servlet.http.Cookie getWrappedCookie() {
//		return wrappedCookie;
//	}
//
//	public void setWrappedCookie(javax.servlet.http.Cookie wrappedCookie) {
//		this.wrappedCookie = wrappedCookie;
//	}
//}