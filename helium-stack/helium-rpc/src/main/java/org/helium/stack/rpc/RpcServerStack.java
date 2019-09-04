package org.helium.stack.rpc;



import org.helium.framework.BeanContext;
import org.helium.framework.route.ServerUrl;
import org.helium.framework.servlet.ServletDescriptor;
import org.helium.framework.servlet.ServletStack;
import org.helium.rpc.channel.tcp.RpcTcpServerChannel;
import org.helium.rpc.server.RpcServiceBootstrap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coral on 8/27/15.
 */
public class RpcServerStack implements ServletStack {

	private String id;
	private int port;
	private String host = "127.0.0.1";
	private RpcTcpServerChannel channel;
	private RpcServiceBootstrap  rpcServiceBootstrap = RpcServiceBootstrap.INSTANCE;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public List<ServerUrl> getServerUrls() {
		List<ServerUrl> list = new ArrayList<>();
		list.add(ServerUrl.parse("tcp://" + host + ":" + port + ";protocol=rpc"));
		return list;
	}

	@Override
	public void start() throws Exception {
		channel = new RpcTcpServerChannel(port);
		rpcServiceBootstrap.registerChannel(channel);
		rpcServiceBootstrap.start();
	}

	@Override
	public void stop() throws Exception {
		rpcServiceBootstrap.stop();
	}

	@Override
	public boolean isSupportServlet(Object servlet) {
		return false;
	}

	@Override
	public boolean isSupportModule(Object module) {
		return false;
	}

	@Override
	public ServletDescriptor getServletDescriptor() {
		return null;
	}

	@Override
	public void registerModule(BeanContext context) {

	}

	@Override
	public void registerServlet(BeanContext context) {

	}

	@Override
	public void unregisterModule(BeanContext context) {

	}

	@Override
	public void unregisterServlet(BeanContext context) {

	}
	@Override
	public String getHost() {
		return host;
	}
}
