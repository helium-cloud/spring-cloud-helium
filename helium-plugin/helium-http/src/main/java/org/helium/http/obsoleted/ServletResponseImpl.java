package org.helium.http.obsoleted;//package org.helium.http.servlet.extension.spi;
//
//import org.glassfish.grizzly.ThreadCache;
//import org.glassfish.grizzly.http.io.NIOOutputStream;
//import org.glassfish.grizzly.http.server.Response;
//
//import javax.servlet.ServletOutputStream;
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Locale;
//
///**
// * @author Will.jingmiao
// * @version 创建时间：2014年9月24日 类说明
// */
//class ServletResponseImpl implements HttpServletResponse {
//
//    protected Response response = null;
//
//    private final ServletOutputStreamImpl outputStream;
//    private ServletWriterImpl writer;
//
//    protected boolean usingOutputStream = false;
//
//    protected boolean usingWriter = false;
//
//    private static final ThreadCache.CachedTypeIndex<ServletResponseImpl> CACHE = ThreadCache.obtainIndex(ServletResponseImpl.class, 2);
//
//    public static ServletResponseImpl create() {
//        ServletResponseImpl response = ThreadCache.takeFromCache(CACHE);
//        if (response == null) {
//            response = new ServletResponseImpl();
//        }
//        return response;
//    }
//
//    protected ServletResponseImpl() {
//        outputStream = new ServletOutputStreamImpl(this);
//    }
//
//    protected ServletRequestImpl servletRequest;
//
//    public void initialize(final Response response, final ServletRequestImpl servletRequest) throws IOException {
//        this.response = response;
//        this.servletRequest = servletRequest;
//        outputStream.initialize();
//    }
//
//    @Override
//    protected Object clone() throws CloneNotSupportedException {
//        throw new CloneNotSupportedException();
//    }
//
//    @Override
//    public String getCharacterEncoding() {
//        if (response == null) {
//            throw new IllegalStateException("Null response object");
//        }
//        return response.getCharacterEncoding();
//    }
//
//    @Override
//    public String getContentType() {
//        if (response == null) {
//            throw new IllegalStateException("Null response object");
//        }
//        return response.getContentType();
//    }
//
//    @Override
//    public ServletOutputStream getOutputStream() throws IOException {
//        if (usingWriter) {
//            throw new IllegalStateException("Illegal attempt to call getOutputStream() after getWriter() has already been called.");
//        }
//        usingOutputStream = true;
//        return outputStream;
//
//    }
//
////    void recycle() {
////        response = null;
////        servletRequest = null;
////        writer = null;
////        outputStream.recycle();
////        usingOutputStream = false;
////        usingWriter = false;
////    }
//
//    @Override
//    public PrintWriter getWriter() throws IOException {
//        if (usingOutputStream) {
//            throw new IllegalStateException("Illegal attempt to call getWriter() after getOutputStream has already been called.");
//        }
//        usingWriter = true;
//        if (writer == null) {
//            writer = new ServletWriterImpl(response.getWriter());
//        }
//        return writer;
//
//    }
//
//    @Override
//    public void setCharacterEncoding(String charset) {
//        if (isCommitted()) {
//            return;
//        }
//        response.setCharacterEncoding(charset);
//    }
//
//    @Override
//    public void setContentLength(int len) {
//        if (isCommitted()) {
//            return;
//        }
//        response.setContentLength(len);
//
//    }
//
//    @Override
//    public void setContentLengthLong(long len) {
//        if (isCommitted()) {
//            return;
//        }
//        response.setContentLengthLong(len);
//    }
//
//    @Override
//    public void setContentType(String type) {
//        if (isCommitted()) {
//            return;
//        }
//        response.setContentType(type);
//    }
//
//    @Override
//    public void setBufferSize(int size) {
//        if (isCommitted()) {
//            throw new IllegalStateException("Illegal attempt to adjust the buffer size after the response has already been committed.");
//        }
//        response.setBufferSize(size);
//
//    }
//
//    @Override
//    public int getBufferSize() {
//        if (response == null) {
//            throw new IllegalStateException("Null response object");
//        }
//
//        return response.getBufferSize();
//    }
//
//    @Override
//    public void flushBuffer() throws IOException {
//        response.flush();
//    }
//
//    @Override
//    public void resetBuffer() {
//        if (isCommitted()) {
//            throw new IllegalStateException("Illegal attempt to reset the buffer after the response has already been committed.");
//        }
//        response.resetBuffer();
//
//    }
//
//    @Override
//    public boolean isCommitted() {
//        if (response == null) {
//            throw new IllegalStateException("Null response object");
//        }
//        return response.isCommitted();
//    }
//
//    @Override
//    public void reset() {
//        if (isCommitted()) {
//            throw new IllegalStateException("Illegal attempt to reset the response after it has already been committed.");
//        }
//        response.reset();
//    }
//
//    @Override
//    public void setLocale(Locale loc) {
//        if (isCommitted()) {
//            return;
//        }
//        response.setLocale(loc);
//    }
//
//    @Override
//    public Locale getLocale() {
//        if (response == null) {
//            throw new IllegalStateException("Null response object");
//        }
//        return response.getLocale();
//    }
//
//    @Override
//    public void addCookie(Cookie cookie) {
//        if (isCommitted()) {
//            return;
//        }
//        ServletCookieWrapper wrapper = new ServletCookieWrapper(cookie.getName(), cookie.getValue());
//        wrapper.setWrappedCookie(cookie);
//        response.addCookie(wrapper);
//    }
//
//    @Override
//    public boolean containsHeader(String name) {
//        if (response == null) {
//            throw new IllegalStateException("Null response object");
//        }
//        return response.containsHeader(name);
//    }
//
//    @Override
//    public String encodeURL(String url) {
//        if (response == null) {
//            throw new IllegalStateException("Null response object");
//        }
//        return response.encodeURL(url);
//    }
//
//    @Override
//    public String encodeRedirectURL(String url) {
//        if (response == null) {
//            throw new IllegalStateException("Null response object");
//        }
//        return response.encodeRedirectURL(url);
//    }
//
//    @Override
//    public String encodeUrl(String url) {
//        return encodeURL(url);
//    }
//
//    @Override
//    public String encodeRedirectUrl(String url) {
//        return encodeRedirectURL(url);
//    }
//
//    @Override
//    public void sendError(int sc, String msg) throws IOException {
//        if (isCommitted()) {
//            throw new IllegalStateException("Illegal attempt to call sendError() after the response has been committed.");
//        }
//        response.sendError(sc, msg);
//    }
//
//    @Override
//    public void sendError(int sc) throws IOException {
//        if (isCommitted()) {
//            throw new IllegalStateException("Illegal attempt to call sendError() after the response has already been committed.");
//        }
//        response.sendError(sc);
//    }
//
//    @Override
//    public void sendRedirect(String location) throws IOException {
//        if (isCommitted()) {
//            throw new IllegalStateException("Illegal attempt to redirect the response after it has been committed.");
//        }
//        response.sendRedirect(location);
//    }
//
//    @Override
//    public void setDateHeader(String name, long date) {
//        if (isCommitted()) {
//            return;
//        }
//        response.setDateHeader(name, date);
//    }
//
//    @Override
//    public void addDateHeader(String name, long date) {
//        if (isCommitted()) {
//            return;
//        }
//        response.addDateHeader(name, date);
//    }
//
//    @Override
//    public void setHeader(String name, String value) {
//        if (isCommitted()) {
//            return;
//        }
//        response.setHeader(name, value);
//    }
//
//    @Override
//    public void addHeader(String name, String value) {
//        if (isCommitted()) {
//            return;
//        }
//        response.addHeader(name, value);
//    }
//
//    @Override
//    public void setIntHeader(String name, int value) {
//        if (isCommitted()) {
//            return;
//        }
//        response.setIntHeader(name, value);
//    }
//
//    @Override
//    public void addIntHeader(String name, int value) {
//        if (isCommitted()) {
//            return;
//        }
//        response.addIntHeader(name, value);
//    }
//
//    @Override
//    public void setStatus(int sc) {
//        if (isCommitted()) {
//            return;
//        }
//        response.setStatus(sc);
//    }
//
//    @Override
//    public void setStatus(int sc, String sm) {
//        if (isCommitted()) {
//            return;
//        }
//        response.setStatus(sc, sm);
//    }
//
//    @Override
//    public int getStatus() {
//        if (response == null) {
//            throw new IllegalStateException("Null response object");
//        }
//        return response.getStatus();
//    }
//
//    @Override
//    public String getHeader(String name) {
//        if (response == null) {
//            throw new IllegalStateException("Null response object");
//        }
//        return response.getHeader(name);
//    }
//
//    @Override
//    public Collection<String> getHeaders(String name) {
//        if (response == null) {
//            throw new IllegalStateException("Null response object");
//        }
//        return Arrays.asList(response.getHeaderValues(name));
//    }
//
//    @Override
//    public Collection<String> getHeaderNames() {
//        if (response == null) {
//            throw new IllegalStateException("Null response object");
//        }
//        return Arrays.asList(response.getHeaderNames());
//    }
//
//    public NIOOutputStream createOutputStream() {
//        if (response == null) {
//            throw new IllegalStateException("Null response object");
//        }
//        return response.createOutputStream();
//    }
//}
