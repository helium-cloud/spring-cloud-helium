package org.helium.http.obsoleted;//package org.helium.http.servlet.extension.spi;
//
//import org.glassfish.grizzly.ThreadCache;
//import org.glassfish.grizzly.http.Cookie;
//import org.glassfish.grizzly.http.io.NIOInputStream;
//import org.glassfish.grizzly.http.server.Request;
//import org.glassfish.grizzly.http.server.Session;
//import org.glassfish.grizzly.http.server.util.Enumerator;
//
//import javax.servlet.*;
//import javax.servlet.http.*;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.util.Collection;
//import java.util.Enumeration;
//import java.util.Locale;
//import java.util.Map;
//
///**
// * @author Will.jingmiao
// * @version 创建时间：2014年9月24日 类说明
// */
//class ServletRequestImpl implements HttpServletRequest {
//
//    protected Request request = null;
//
//    protected ServletResponseImpl servletResponse;
//
//    private final ServletInputStreamImpl inputStream;
//    private ServletReaderImpl reader;
//
//    private SessionImpl httpSession = null;
//
//    private String contextPath = "";
//    private String servletPath = "";
//
//    private String pathInfo;
//
//    protected boolean usingInputStream = false;
//
//    protected boolean usingReader = false;
//
//    private static final ThreadCache.CachedTypeIndex<ServletRequestImpl> CACHE_IDX = ThreadCache.obtainIndex(ServletRequestImpl.class, 2);
//
//    public static ServletRequestImpl create() {
//        final ServletRequestImpl request = ThreadCache.takeFromCache(CACHE_IDX);
//        if (request != null) {
//            return request;
//        }
//
//        return new ServletRequestImpl();
//    }
//
//    protected ServletRequestImpl() {
//        // 初始化入口流
//        this.inputStream = new ServletInputStreamImpl(this);
//    }
//
//    public void init(final Request request, final ServletResponseImpl servletResponse) throws IOException {
//        this.request = request;
//        this.servletResponse = servletResponse;
//        inputStream.initialize();
//    }
//
//    @Override
//    protected Object clone() throws CloneNotSupportedException {
//        throw new CloneNotSupportedException();
//    }
//
//    @Override
//    public Object getAttribute(String name) {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getAttribute(name);
//    }
//
//    @Override
//    public Enumeration<String> getAttributeNames() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return new Enumerator<String>(request.getAttributeNames());
//    }
//
//    @Override
//    public String getCharacterEncoding() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getCharacterEncoding();
//    }
//
//    @Override
//    public void setCharacterEncoding(String env) throws java.io.UnsupportedEncodingException {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        request.setCharacterEncoding(env);
//    }
//
//    @Override
//    public int getContentLength() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getContentLength();
//    }
//
//    @Override
//    public long getContentLengthLong() {
//        return 0;
//    }
//
//    @Override
//    public String getContentType() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getContentType();
//    }
//
//    @Override
//    public ServletInputStream getInputStream() throws IOException {
//        if (usingReader) {
//            throw new IllegalStateException("Illegal attempt to call getInputStream() after getReader() has already been called.");
//        }
//        usingInputStream = true;
//
//        return inputStream;
//    }
//
////    void recycle() {
////        request = null;
////        servletResponse = null;
////        reader = null;
////
////        inputStream.recycle();
////
////        usingInputStream = false;
////        usingReader = false;
////
////    }
//
//    @Override
//    public String getParameter(String name) {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getParameter(name);
//    }
//
//    @Override
//    public Enumeration<String> getParameterNames() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return new Enumerator<String>(request.getParameterNames());
//    }
//
//    @Override
//    public String[] getParameterValues(String name) {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getParameterValues(name);
//
//    }
//
//    @Override
//    public Map<String, String[]> getParameterMap() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getParameterMap();
//    }
//
//    @Override
//    public String getProtocol() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getProtocol().getProtocolString();
//    }
//
//    @Override
//    public String getScheme() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getScheme();
//    }
//
//    @Override
//    public String getServerName() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getServerName();
//    }
//
//    @Override
//    public int getServerPort() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getServerPort();
//    }
//
//    @Override
//    public BufferedReader getReader() throws IOException {
//        if (usingInputStream) {
//            throw new IllegalStateException("Illegal attempt to call getReader() after getInputStream() has already been called.");
//        }
//
//        usingReader = true;
//        if (reader == null) {
//            reader = new ServletReaderImpl(request.getReader());
//        }
//
//        return reader;
//
//    }
//
//    @Override
//    public String getRemoteAddr() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getRemoteAddr();
//    }
//
//    @Override
//    public String getRemoteHost() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getRemoteHost();
//    }
//
//    @Override
//    public void setAttribute(String name, Object value) {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        request.setAttribute(name, value);
//
//    }
//
//    @Override
//    public void removeAttribute(String name) {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//        request.removeAttribute(name);
//    }
//
//    @Override
//    public Locale getLocale() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//        return request.getLocale();
//    }
//
//    @Override
//    public Enumeration<Locale> getLocales() {
//
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return new Enumerator<Locale>(request.getLocales());
//    }
//
//    @Override
//    public boolean isSecure() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//        return request.isSecure();
//    }
//
//    @Override
//    public RequestDispatcher getRequestDispatcher(String path) {
//        return null;
//    }
//
//    @Override
//    public String getRealPath(String path) {
//        return null;
//    }
//
//    @Override
//    public String getAuthType() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//        return request.getAuthType();
//    }
//
//    public Cookie[] getGrizzlyCookies() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//        return request.getCookies();
//    }
//
//    @Override
//    public long getDateHeader(String name) {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//        return request.getDateHeader(name);
//    }
//
//    @Override
//    public String getHeader(String name) {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//        return request.getHeader(name);
//    }
//
//    @Override
//    public Enumeration<String> getHeaders(String name) {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//        return new Enumerator<String>(request.getHeaders(name).iterator());
//    }
//
//    @Override
//    public Enumeration<String> getHeaderNames() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return new Enumerator<String>(request.getHeaderNames().iterator());
//    }
//
//    @Override
//    public int getIntHeader(String name) {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getIntHeader(name);
//    }
//
//    @Override
//    public String getMethod() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getMethod().getMethodString();
//    }
//
//    @Override
//    public String getPathInfo() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return pathInfo;
//    }
//
//    @Override
//    public String getPathTranslated() {
//        return null;
//    }
//
//    @Override
//    public String getContextPath() {
//        return contextPath;
//    }
//
//    protected void setContextPath(String contextPath) {
//        if (contextPath == null) {
//            this.contextPath = "";
//        } else {
//            this.contextPath = contextPath;
//        }
//    }
//
//    @Override
//    public String getQueryString() {
//
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getQueryString();
//    }
//
//    @Override
//    public String getRemoteUser() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getRemoteUser();
//    }
//
//    @Override
//    public boolean isUserInRole(String role) {
//        throw new IllegalStateException("Not yet implemented");
//    }
//
//    @Override
//    public java.security.Principal getUserPrincipal() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getUserPrincipal();
//    }
//
//    @Override
//    public String getRequestedSessionId() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getRequestedSessionId();
//    }
//
//    @Override
//    public String getRequestURI() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getRequestURI();
//    }
//
//    @Override
//    public StringBuffer getRequestURL() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return Request.appendRequestURL(request, new StringBuffer());
//    }
//
//    @Override
//    public String getServletPath() {
//        return servletPath;
//    }
//
//    public void initSession() {
//        Session session = request.getSession(false);
//        if (session != null) {
//            httpSession = new SessionImpl();
//            httpSession.setSession(session);
//            httpSession.access();
//        }
//    }
//
//    @Override
//    public HttpSession getSession(boolean create) {
//        if (httpSession == null && create) {
//            httpSession = new SessionImpl();
//        }
//
//        if (httpSession != null) {
//            Session session = request.getSession(create);
//            if (session != null) {
//                httpSession.setSession(session);
//                httpSession.access();
//            } else {
//                return null;
//            }
//        }
//        return httpSession;
//    }
//
//    @Override
//    public HttpSession getSession() {
//
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return getSession(true);
//    }
//
//    @Override
//    public String changeSessionId() {
//        return null;
//    }
//
//    @Override
//    public boolean isRequestedSessionIdValid() {
//        return request.isRequestedSessionIdValid();
//    }
//
//    @Override
//    public boolean isRequestedSessionIdFromCookie() {
//        return request.isRequestedSessionIdFromCookie();
//    }
//
//    @Override
//    public boolean isRequestedSessionIdFromURL() {
//        return request.isRequestedSessionIdFromURL();
//    }
//
//    @Override
//    public boolean isRequestedSessionIdFromUrl() {
//        return isRequestedSessionIdFromURL();
//    }
//
//    @Override
//    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
//        // TODO Servlet3.x 框架级身份认证机制
//        return false;
//    }
//
//    @Override
//    public void login(String username, String password) throws ServletException {
//        // TODO Servlet3.x 框架级身份认证机制
//    }
//
//    @Override
//    public void logout() throws ServletException {
//        // TODO Servlet3.x 框架级身份认证机制
//    }
//
//    @Override
//    public Collection<Part> getParts() throws IOException, ServletException {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//        if (!"multipart/form-data".equals(request.getHeader("Content-Type"))) {
//            throw new ServletException("Request is not of type 'multipart/form-data'");
//        }
//        // TODO Servlet3.x multipart/form-data 支持
//        return null;
//    }
//
//    @Override
//    public Part getPart(String name) throws IOException, ServletException {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//        if (!"multipart/form-data".equals(request.getHeader("Content-Type"))) {
//            throw new ServletException("Request is not of type 'multipart/form-data'");
//        }
//        // TODO Servlet3.x multipart/form-data 支持
//        return null;
//    }
//
//    @Override
//    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
//        // TODO Servlet3.x HTTP 协议升级
//        return null;
//    }
//
//    public boolean isUpgraded() {
//        // TODO Servlet3.x HTTP 协议升级 (ServletInputStreamImpl & ServletOutputStreamImpl 需要判断是否upgraded，暂且加上这么一个判断方法)
//        return false;
//    }
//
//    @Override
//    public javax.servlet.http.Cookie[] getCookies() {
//        final Cookie[] internalCookies = request.getCookies();
//        if (internalCookies == null) {
//            return null;
//        }
//        javax.servlet.http.Cookie[] cookies = new javax.servlet.http.Cookie[internalCookies.length];
//        for (int i = 0; i < internalCookies.length; i++) {
//            final Cookie cook = internalCookies[i];
//            if (cook instanceof ServletCookieWrapper) {
//                cookies[i] = ((ServletCookieWrapper) internalCookies[i]).getWrappedCookie();
//            } else {
//                cookies[i] = new javax.servlet.http.Cookie(cook.getName(), cook.getValue());
//                cookies[i].setComment(cook.getComment());
//                if (cook.getDomain() != null) {
//                    cookies[i].setDomain(cook.getDomain());
//                }
//                cookies[i].setMaxAge(cook.getMaxAge());
//                cookies[i].setPath(cook.getPath());
//                cookies[i].setSecure(cook.isSecure());
//                cookies[i].setVersion(cook.getVersion());
//            }
//        }
//        return cookies;
//    }
//
//    @Override
//    public int getRemotePort() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getRemotePort();
//    }
//
//    @Override
//    public String getLocalName() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getLocalName();
//    }
//
//    @Override
//    public String getLocalAddr() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getLocalAddr();
//    }
//
//    @Override
//    public int getLocalPort() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//
//        return request.getLocalPort();
//    }
//
//    @Override
//    public ServletContext getServletContext() {
//        // TODO Servlet3.x 调度 获取本ServletRequest最后一次dispatched的Servlet上下文
//        return null;
//    }
//
//    @Override
//    public AsyncContext startAsync() throws IllegalStateException {
//        // TODO Servlet3.x 异步模式
//        return null;
//    }
//
//    @Override
//    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
//        // TODO Servlet3.x 异步模式
//        return null;
//    }
//
//    @Override
//    public boolean isAsyncStarted() {
//        // TODO Servlet3.x 异步模式
//        return false;
//    }
//
//    @Override
//    public boolean isAsyncSupported() {
//        // TODO Servlet3.x 异步模式
//        return false;
//    }
//
//    @Override
//    public AsyncContext getAsyncContext() {
//        // TODO Servlet3.x 异步模式
//        return null;
//    }
//
//    @Override
//    public DispatcherType getDispatcherType() {
//        // TODO Servlet3.x 调度
//        return null;
//    }
//
//    public void setServletPath(final String servletPath) {
//        if (servletPath != null) {
//            if (servletPath.length() == 0) {
//                this.servletPath = "";
//            } else {
//                this.servletPath = servletPath;
//            }
//        }
//    }
//
//    public NIOInputStream createInputStream() {
//        if (request == null) {
//            throw new IllegalStateException("Null request object");
//        }
//        return request.createInputStream();
//    }
//}
