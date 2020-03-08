package org.helium.http.obsoleted;//package org.helium.http.servlet.extension.spi;
//
//
//import org.glassfish.grizzly.ReadHandler;
//import org.glassfish.grizzly.http.io.NIOInputStream;
//import org.glassfish.grizzly.localization.LogMessages;
//
//import javax.servlet.ReadListener;
//import javax.servlet.ServletInputStream;
//import java.io.IOException;
//
///**
// * 实现Javax.Servlet中ServletInputStream接口
// *
// * @author Will.jingmiao
// * @version 创建时间：2014年9月24日
// */
//class ServletInputStreamImpl extends ServletInputStream {
//
//    private final ServletRequestImpl servletRequest;
//    private NIOInputStream inputStream;
//
//    private ReadHandler readHandler;
//
////    private boolean prevIsReady = true;
//
//    protected ServletInputStreamImpl(final ServletRequestImpl servletRequest) {
//        this.servletRequest = servletRequest;
//    }
//
//    public void initialize() throws IOException {
//        this.inputStream = servletRequest.createInputStream();
//    }
//
//    @Override
//    public int read() throws IOException {
//        if (!isReady()) {
//            throw new IllegalStateException(LogMessages.WARNING_GRIZZLY_HTTP_SERVLET_NON_BLOCKING_ERROR());
//        }
//
//        return inputStream.read();
//    }
//
//    @Override
//    public int available() throws IOException {
//        if (!isReady()) {
//            return 0;
//        }
//
//        return inputStream.available();
//    }
//
//    @Override
//    public int read(final byte[] b) throws IOException {
//        if (!isReady()) {
//            throw new IllegalStateException(LogMessages.WARNING_GRIZZLY_HTTP_SERVLET_NON_BLOCKING_ERROR());
//        }
//
//        return inputStream.read(b, 0, b.length);
//    }
//
//    @Override
//    public int read(final byte[] b, final int off, final int len) throws IOException {
//        if (!isReady()) {
//            throw new IllegalStateException(LogMessages.WARNING_GRIZZLY_HTTP_SERVLET_NON_BLOCKING_ERROR());
//        }
//
//        return inputStream.read(b, off, len);
//    }
//
//    @Override
//    public long skip(final long n) throws IOException {
//        return inputStream.skip(n);
//    }
//
//    @Override
//    public void mark(final int readlimit) {
//        inputStream.mark(readlimit);
//    }
//
//    @Override
//    public void reset() throws IOException {
//        inputStream.reset();
//    }
//
//    @Override
//    public boolean markSupported() {
//        return inputStream.markSupported();
//    }
//
//    @Override
//    public void close() throws IOException {
//        inputStream.close();
//    }
//
////    void recycle() {
////        inputStream = null;
////        prevIsReady = true;
////    }
//
//    @Override
//    public boolean isFinished() {
//        return inputStream.isFinished();
//    }
//
//    @Override
//    public boolean isReady() {
//        return inputStream.isReady();
//    }
//
//    @Override
//    public void setReadListener(ReadListener readListener) {
//        if (readListener == null) {
//            throw new NullPointerException("Parameter 'readListener' must not null");
//        }
//        if (servletRequest.isAsyncStarted()) {
//            throw new IllegalStateException("Request is async started");
//        }
//        if (servletRequest.isUpgraded()) {
//            throw new IllegalStateException("Request is upgraded");
//        }
//        if (this.readHandler != null) {
//            throw new IllegalStateException("Only can set ReadListener once for same request");
//        }
//        this.readHandler = new ReadHandler() {
//            @Override
//            public void onDataAvailable() throws Exception {
//                readListener.onDataAvailable();
//            }
//
//            @Override
//            public void onError(Throwable t) {
//                readListener.onError(t);
//            }
//
//            @Override
//            public void onAllDataRead() throws Exception {
//                readListener.onAllDataRead();
//            }
//        };
//        inputStream.notifyAvailable(readHandler);
//    }
//}
