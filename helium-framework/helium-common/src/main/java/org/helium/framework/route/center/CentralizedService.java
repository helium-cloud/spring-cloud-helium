package org.helium.framework.route.center;

import org.helium.framework.BeanContext;
import org.helium.framework.BeanContextService;
import org.helium.framework.annotations.ServiceInterface;
import org.helium.framework.entitys.BeanConfiguration;
import org.helium.framework.entitys.FactorGroupNode;
import org.helium.framework.entitys.dashboard.WorkerJson;
import org.helium.framework.route.ServerEndpoint;
import org.helium.framework.route.ServerRouter;
import org.helium.framework.route.ServerUrl;

import java.util.List;

/**
 * M8 Release 3 implements: 实现中心化的
 * Created by Coral on 6/7/15.
 */
@ServiceInterface(id = "helium:CentralizedService")
public interface CentralizedService {
	/**
	 * 获取本机启动的信息
	 * @return
	 */
	ServerEndpoint getServerEndpoint();

	/**
	 * 获取server节点
	 * @return
	 */
	List<ServerEndpoint> getServerEndpointList();

	/**
	 * 获取grayserver节点
	 * @return
	 */
	List<ServerEndpoint> getGrayServerEndpointList();

	/**
	 * 注册本机服务并连接服务器
	 * @throws Exception
	 */
	void register(String serverId, List<ServerUrl> urls, BeanContextService contextService) throws Exception;

	/**
	 * 将集群中的References信息同步到BeanContextService中, 并建立同步关系
	 */
	void syncReferences(BeanContextService service);

	/**
	 * 注册Bundle信息
	 * @param
	 */
	void registerBundle(String bundleName, String bundleVersion, List<BeanConfiguration> beans) throws Exception;

	/**
	 * 注册灰度节点
	 * @param
	 */
	void registerGrayBundle(String bundleName, String bundleVersion, FactorGroupNode factors, List<BeanConfiguration> beans) throws Exception;

	/**
	 * 注销Bundle信息
	 * @param bundleName
	 * @param bundleVersion
	 */
	void unregisterBundle(String bundleName, String bundleVersion) throws Exception;

	/**
	 * 注销灰度节点的信息
	 * @param bundleName
	 * @param bundleVersion
	 */
	void unregisterGrayBundle(String bundleName, String bundleVersion) throws Exception;

	/**
	 * 获取一个特定Bean的id
	 * @return
	 */
	ServerRouter subscribeServerRouter(BeanContext bc, String bundleName, String protocol);

	/**
	 * 获取一个特定的Router
	 * @param bundleName
	 * @return
	 */
	ServerRouter getServerRouter(String bundleName, String protocol);

	/**
	 * 临时提供给Dashboard获取Worker的状态
	 * @return
	 */
	List<WorkerJson> getWorkers();
}
