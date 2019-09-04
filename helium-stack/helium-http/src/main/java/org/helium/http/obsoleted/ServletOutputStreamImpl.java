//package org.helium.http.servlet.extension.spi;
//
//import org.glassfish.grizzly.WriteHandler;
//import org.glassfish.grizzly.http.io.NIOOutputStream;
//import org.glassfish.grizzly.localization.LogMessages;
//
//import javax.servlet.ServletOutputStream;
//import javax.servlet.WriteListener;
//import java.io.IOException;
//
///**
// * @author coral
// * @version 创建时间：2014年9月24日 类说明
// */
//class ServletOutputStreamImpl extends ServletOutputStream {
//
//    private final ServletResponseImpl servletResponse;
//    private NIOOutputStream outputStream;
//
//    WriteHandler writeHandler;
//
//    protected ServletOutputStreamImpl(final ServletResponseImpl servletResponse) {
//        this.servletResponse = servletResponse;
//    }
//
//    protected void initialize() throws IOException {
//        this.outputStream = servletResponse.createOutputStream();
//    }
//
//    @Override
//    public void write(int i) throws IOException {
//        if (!isReady()) {
//            throw new IllegalStateException(LogMessages.WARNING_GRIZZLY_HTTP_SERVLET_NON_BLOCKING_ERROR());
//        }
//
//        outputStream.write(i);
//    }
//
//    @Override
//    public void write(byte[] b) throws IOException {
//        write(b, 0, b.length);
//    }
//
//    @Override
//    public void write(byte[] b, int off, int len) throws IOException {
//        if (!isReady()) {
//            throw new IllegalStateException(LogMessages.WARNING_GRIZZLY_HTTP_SERVLET_NON_BLOCKING_ERROR());
//        }
//
//        outputStream.write(b, off, len);
//    }
//
//    @Override
//    public void flush() throws IOException {
//        if (!isReady()) {
//            throw new IllegalStateException(LogMessages.WARNING_GRIZZLY_HTTP_SERVLET_NON_BLOCKING_ERROR());
//        }
//
//        outputStream.flush();
//    }
//
//    @Override
//    public void close() throws IOException {
//        if (!isReady()) {
//            throw new IllegalStateException(LogMessages.WARNING_GRIZZLY_HTTP_SERVLET_NON_BLOCKING_ERROR());
//        }
//
//        outputStream.close();
//    }
//
////	void recycle() {
////		outputStream = null;
////		prevIsReady = true;
////	}
//
//    @Override
//    public boolean isReady() {
//        return outputStream.canWrite();
//    }
//
//    @Override
//    public void setWriteListener(WriteListener writeListener) {
//        if (writeListener == null) {
//            throw new NullPointerException("Parameter 'writeListener' must not null");
//        }
//        if (writeHandler != null) {
//            throw new IllegalStateException("Only can set WriteListener once for same response");
//        }
//        if (servletResponse.servletRequest.isAsyncStarted()) {
//            throw new IllegalStateException("Request is async started");
//        }
//        if (servletResponse.servletRequest.isUpgraded()) {
//            throw new IllegalStateException("Request is upgraded");
//        }
//        writeHandler = new WriteHandler() {
//            @Override
//            public void onWritePossible() throws Exception {
//                writeListener.onWritePossible();
//            }
//
//            @Override
//            public void onError(Throwable t) {
//                writeListener.onError(t);
//            }
//        };
//        outputStream.notifyCanWrite(writeHandler);
//    }
//}
