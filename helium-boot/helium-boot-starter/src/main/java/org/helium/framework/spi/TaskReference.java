package org.helium.framework.spi;

import com.feinno.superpojo.SuperPojo;
import org.helium.framework.configuration.BeanContextProvider;
import org.helium.framework.entitys.BeanConfiguration;
import org.helium.framework.route.ServerRouter;
import org.helium.framework.route.ServerUrl;
import org.helium.framework.spi.task.RouterTaskArgs;
import org.helium.framework.spi.task.TaskConsumerRpcService;
import org.helium.framework.task.DedicatedTaskArgs;
import org.helium.framework.task.TaskBeanContext;
import org.helium.rpc.channel.tcp.RpcTcpEndpoint;
import org.helium.rpc.client.RpcMethodStub;
import org.helium.rpc.client.RpcProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Coral on 9/12/15.
 */
public class TaskReference extends BeanReference implements TaskBeanContext {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskReference.class);

	private ServerRouter router;
	private String eventName;
	private String storageType;
	private int partition;

	public TaskReference(BeanConfiguration configuration, BeanContextProvider cp) {
		super(configuration, cp);
	}

	@Override
	protected void resolve() {
		this.eventName = getConfiguration().getEvent();
		this.storageType = getConfiguration().getStorageType();
	}

	@Override
	public ServerRouter getRouter() {
		return router;
	}

	@Override
	public Object getBean() {
		return null;
	}

	public void setRouter(ServerRouter router) {
		this.router = router;
	}

	@Override
	public String getEventName() {
		return eventName;
	}

	@Override
	public String getStorageType() {
		return storageType;
	}
	

	@Override
	public void consume(Object args) {
		ServerUrl url = router.pickServer();
		consumeByRpc(url, eventName, getId().toString(), args);
	}

	public static void consumeByRpc(ServerUrl url, String eventName, String beanId, Object args) {
		RpcTcpEndpoint ep = RpcTcpEndpoint.parse(url.getUrl());
		try {
			RpcMethodStub stub = RpcProxyFactory.getMethodStub(ep, TaskConsumerRpcService.SERVICE_NAME, "consumeArgs");
			RouterTaskArgs ra = new RouterTaskArgs();
			ra.setBeanId(beanId);
			ra.setEventId(eventName);
			if (args instanceof DedicatedTaskArgs) {
				ra.setTag(((DedicatedTaskArgs) args).getTag());
			}
			ra.setArgsData(((SuperPojo) args).toPbByteArray());
			stub.invoke(ra);
		} catch (Exception ex) {
			LOGGER.error("consumeByRpc through {} failed: {}", ep, ex);
		}
	}

}
