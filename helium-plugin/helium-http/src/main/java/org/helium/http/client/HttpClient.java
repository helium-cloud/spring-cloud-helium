package org.helium.http.client;

import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.perfmon.Stopwatch;
import com.feinno.superpojo.type.DateTime;
import org.helium.threading.Future;
import org.helium.util.StringUtils;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A asynchronous HTTP client implements with Netty http应答回来后进入一个工作线程池
 * <p>
 * 有2个mode:
 * <p>
 * oneConnectionMode http 第一次建立好链接之后，后续所有请求都发送到这个连接上， 所有后续请求应请求一个地址 如果连接中断，
 * 后续请求抛异常，如果上一次请求的应答没有回来，就发下一个，抛异常。关闭连接需要调用close方法
 * <p>
 * 非oneConnectionMode 有一个tcp的连接池供使用，如果连接空闲60s， 出池。 可以并发使用
 *
 * @author liyang
 *         modify by linsu at 10.28.2011
 *         modify by linsu at 11.01.2011
 *         add connectionPool modify sendData support http 1.1 302 跳转功能 modify
 *         by linsu at 3/29/2012
 * @version 1.0
 */

public class HttpClient {

    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);
    private static ClientBootstrap bootstrap;
    private static ExecutorService ioThreadpool;
    private static HttpConnectionPool connPool;
    // private static ScalableExecutor executor;
    protected static HashWheelTimer timer;

    //	private Channel channel = null;
//	private boolean singleConnectionMode = false;
    private DateTime lastAccessTime = DateTime.now();
    private int receivedDataLength = 0;

    private HttpClientCounters counters;

    public HttpClient() {
        this("httpClient");
//		this.singleConnectionMode = singleConnectionMode;
    }

    public HttpClient(String clientName) {
        counters = PerformanceCounterFactory.getCounters(HttpClientCounters.class, clientName);
    }

    static {
        ioThreadpool = Executors.newCachedThreadPool();
        ChannelFactory factory = new NioClientSocketChannelFactory(ioThreadpool, ioThreadpool, 56);

        bootstrap = new ClientBootstrap(factory);

        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", true);
        connPool = new HttpConnectionPool();
        // executor = new ScalableExecutor(50, 200,2000);
        timer = new HashWheelTimer(50, TimeUnit.MILLISECONDS, 7200);
    }

    public void setLastAccessTime(DateTime lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public DateTime getLastAccessTime() {
        return lastAccessTime;
    }

    public int getReceivedDataLength() {
        return receivedDataLength;
    }

    public void setReceivedDataLength(int receivedDataLength) {
        this.receivedDataLength = receivedDataLength;
    }

    // httpmessage 的公厂类方法
    public static HttpClientRequest createHttpRequest(String method, String uri) {

        return createHttpRequest(method, uri, true);
    }

	// httpmessage 的公厂类方法
	public static HttpClientRequest createHttpRequest(String method, String uri, boolean carryIp) {
		URI u;
		try {
			u = new URI(uri);
		} catch (URISyntaxException e) {

			throw new IllegalArgumentException(String.format("wrong uri: %s", uri));
		}
		String scheme = u.getScheme() == null ? "http" : u.getScheme();
		String host = u.getHost() == null ? "localhost" : u.getHost();
		int port = u.getPort() == -1 ? 80 : u.getPort();

		if (!scheme.equalsIgnoreCase("http")) {
			throw new IllegalArgumentException("httpTransfer only support http protocol");
		}

		// String finalUri = (u.getQuery() == null) ? u.getPath() : u.getPath()
		// + "?" + u.getQuery();

		String urlPath = "/";
		if (carryIp){
			urlPath = uri;
		} else {
			urlPath = u.getPath();
			String query = u.getQuery();
			if (!StringUtils.isNullOrEmpty(query)){
				urlPath = urlPath + "?" + query;
			}

		}
		HttpClientRequest req = new HttpClientRequest(HttpVersion.HTTP_1_1, HttpMethod.valueOf(method), urlPath);
		req.headers().add(HttpHeaders.Names.HOST, port == 80 ? host : String.format("%s:%s", host, port));
		req.headers().add(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		req.setRemoteAddress(host);
		req.setRemotePort(port);

		return req;
	}

    /**
     * <p>
     * if downstreamhandler(encode) error or network layer send exception, it
     * will send a upstream exception event and writefuture will also know it.
     *
     * @param req
     * @throws Throwable
     */

    public synchronized Future<HttpClientResponse> sendData(final HttpClientRequest req) throws Throwable {
        String key = String.format("%s:%s", req.getRemoteAddress(), req.getRemotePort());
        Channel channel = connPool.poll(key);


        //final Future<HttpClientResponse> f = new Future<HttpClientResponse>();
        Attachment att = new Attachment(new Future<>(), counters.getTx().begin());

        final String reqUri = req.getUri();
        if (channel == null) {
            ChannelFuture future = bootstrap
                    .connect(new InetSocketAddress(req.getRemoteAddress(), req.getRemotePort()));

            future.addListener(future1 -> {
                if (future1.isSuccess() == false) {
                    Exception exception = new Exception("connect timeout");
                    att.watch.fail(exception);
                    att.future.complete(null, exception);
                    logger.error(reqUri + " connect timeout!");
                    return;
                }
                Channel channel1 = future1.getChannel();
                channel1.getPipeline().addLast("decoder", new KrakenHttpResponseDecoder());
                channel1.getPipeline().addLast("aggregator", new HttpChunkAggregator(10 * 1024 * 1024)); // 最大10m
                channel1.getPipeline().addLast("apphandler", new AppUpstreamHandler());
                channel1.getPipeline().addLast("encoder", new HttpRequestEncoder());

                //channel.setAttachment(f);
                channel1.setAttachment(att);
                doSend(channel1, req);
            });
        } else {
            //channel.setAttachment(f);
            channel.setAttachment(att);
            doSend(channel, req);
        }
        return att.future;
        //return f;
    }

    private void doSend(Channel channel, HttpClientRequest req) {
//        final Future<HttpClientResponse> f = (Future<HttpClientResponse>) channel.getAttachment();
        Attachment att = (Attachment) channel.getAttachment();
        final Channel ch = channel;
        final String reqUri = req.getUri();
        ChannelFuture wfuture = channel.write(req);
        wfuture.addListener(future -> {
            if (future.isSuccess() == false) {
                Exception exception = new Exception("write failed");
                att.watch.fail(exception);
                att.future.complete(null, exception);
                ch.close();
                logger.error(reqUri + " write failed!");
                channel.setAttachment(null);
            }
        });

        final Timeout t = timer.newTimeout(timeout -> {
            if (att.future.isDone() == false) {
                Exception exception = new Exception("timeout");
                att.watch.fail(exception);
                att.future.complete(null, exception);
                ch.close();
                logger.error(reqUri + " timeout!");
                channel.setAttachment(null);
            }
        }, 120 * 1000, TimeUnit.MILLISECONDS); // 请求120秒超时

        att.future.addListener(result -> {
            t.cancel();
            //att.watch.end();//TODO 是否应该记录此问题?
        });
    }

    public void close() {

    }

    class KrakenHttpResponseDecoder extends HttpResponseDecoder {
        @Override
        protected HttpMessage createMessage(String initialLine[]) {
            return new HttpClientResponse(HttpVersion.valueOf(initialLine[0]), new HttpResponseStatus(Integer.valueOf(
                    initialLine[1]).intValue(), initialLine[2]));
        }
    }

    class AppUpstreamHandler extends SimpleChannelUpstreamHandler {
        AppUpstreamHandler() {
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
            // 无论 send过程 ,rev处理过程的
            // io,应用层的exception都在这里汇聚
            /*
             * 此处需要 error 输出ownerId,new date(); message =
			 * strackTrace,根据堆栈能知道哪个transaction出错 req.to
			 * string();if(response!=null) response.toString();
			 */
            logger.error("exception happened on http client send or recive", e.getCause());
            Channel channel = ctx.getChannel();
            channel.close();
        }

        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
            Channel channel = ctx.getChannel();
            //Future<HttpClientResponse> f = (Future<HttpClientResponse>) channel.getAttachment();
            Attachment att = (Attachment) channel.getAttachment();
            channel.setAttachment(null);
            connPool.add(channel);
            att.watch.end();
            att.future.complete((HttpClientResponse) e.getMessage());
        }


    }

    private class Attachment {
        private Future<HttpClientResponse> future;

        private Stopwatch watch;

        public Attachment(Future<HttpClientResponse> future, Stopwatch watch) {
            this.future = future;
            this.watch = watch;
        }
    }

    public static void main(String[] args) {

        // false: page mode, true: large mode
        HttpClient c = new HttpClient();

        HttpClientRequest req = HttpClient.createHttpRequest(HttpMethod.GET.toString(), "http://10.10.220.103:8003/rcs/storage/httpContent/15v3w6qs-7utt-1035-8891-d97324ec3799", false);
		try {
			Future<HttpClientResponse> f = c.sendData(req);
			f.addListener(result -> {
				System.out.print(result.getValue().toString());
				System.out.print("\r\n");
			});
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

}


