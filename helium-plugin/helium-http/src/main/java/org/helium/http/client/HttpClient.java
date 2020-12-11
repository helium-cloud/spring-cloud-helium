package org.helium.http.client;

import com.feinno.superpojo.type.DateTime;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;
import org.helium.http.client.timer.Timeout;
import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.perfmon.Stopwatch;
import org.helium.threading.Future;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
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
 * modify by linsu at 10.28.2011
 * modify by linsu at 11.01.2011
 * add connectionPool modify sendData support http 1.1 302 跳转功能 modify
 * by linsu at 3/29/2012
 * @version 1.0
 */

public class HttpClient {

	private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);
	private static Bootstrap bootstrap;
	private static HttpConnectionPool connPool;
	protected static HashWheelTimer timer;
	private DateTime lastAccessTime = DateTime.now();
	private int receivedDataLength = 0;

	private HttpClientCounters counters;

	private static final AttributeKey<Attachment> RESPONSE_KEY = AttributeKey.valueOf("RESPONSE_KEY");

	public HttpClient() {
		this("httpClient");
	}

	public HttpClient(String clientName) {
		counters = PerformanceCounterFactory.getCounters(HttpClientCounters.class, clientName);
	}

	{


		bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.group(new NioEventLoopGroup(3));
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast("decoder", new KrakenHttpResponseDecoder());
				ch.pipeline().addLast("apphandler", new AppUpstreamHandler());
				ch.pipeline().addLast("encoder", new HttpRequestEncoder());
			}
		});
		connPool = new HttpConnectionPool();
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
		if (carryIp) {
			urlPath = uri;
		} else {
			urlPath = u.getPath();
			String query = u.getQuery();
			if (!StringUtils.isNullOrEmpty(query)) {
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

		Future<HttpClientResponse> clientResponse = new Future<>();
		Stopwatch stopwatch = counters.getTx().begin();
		Attachment attachment = new Attachment(clientResponse, stopwatch);

		final String reqUri = req.getUri();
		if (channel == null) {
			ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(req.getRemoteAddress(), req.getRemotePort()));
			channelFuture.addListener(futureLis -> {
				if (futureLis.isSuccess() == false) {
					Exception exception = new Exception("connect timeout");
					stopwatch.fail(exception);
					clientResponse.complete(null, exception);
					logger.error(reqUri + " connect timeout!");
					return;
				}

				Channel channelInner =  channelFuture.channel();
				channelInner.attr(RESPONSE_KEY).set(attachment);
				doSend(channelInner, req, attachment);
			});
		} else {
			channel.attr(RESPONSE_KEY).set(attachment);
			doSend(channel, req, attachment);
		}
		return clientResponse;
	}

	private void doSend(Channel channel, HttpClientRequest req, Attachment attachment) {

		final Channel ch = channel;
		final String reqUri = req.getUri();
		ChannelFuture wfuture = channel.writeAndFlush(req);
		wfuture.addListener(future -> {
			if (future.isSuccess() == false) {
				Exception exception = new Exception("write failed");
				ch.close();
				attachment.watch.fail(exception);
				attachment.future.complete(null, exception);
				logger.error(reqUri + " write failed!");
				channel.attr(RESPONSE_KEY).getAndSet(null);
			}
		});

		final Timeout t = timer.newTimeout(timeout -> {
			if (attachment.future.isDone() == false) {
				Exception exception = new Exception("timeout");
				attachment.watch.fail(exception);
				attachment.future.complete(null, exception);
				ch.close();
				logger.error(reqUri + " timeout!");
				channel.attr(RESPONSE_KEY).getAndSet(null);
			}
		}, 120 * 1000, TimeUnit.MILLISECONDS); // 请求120秒超时

		attachment.future.addListener(result -> {
			t.cancel();
			logger.warn(reqUri + " t.cancel()");
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

	class AppUpstreamHandler extends ChannelInboundHandlerAdapter {
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			if (msg instanceof HttpResponse) {
				HttpResponse response = (HttpResponse) msg;
			}
			if (msg instanceof HttpContent) {
				HttpContent content = (HttpContent) msg;
				ByteBuf buf = content.content();
				buf.release();
			}
			connPool.add(ctx.channel());
			Attachment attachment = ctx.channel().attr(RESPONSE_KEY).getAndSet(null);
			if (attachment != null){
				attachment.watch.end();
				attachment.future.complete((HttpClientResponse) msg);
			}

		}
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception {
			Attachment attachment = ctx.channel().attr(RESPONSE_KEY).getAndSet(null);
			attachment.watch.end();
			attachment.future.complete(null, new Exception(cause.getMessage()));
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



}


