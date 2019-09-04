package zconfig.configuration;


import org.helium.rpc.server.RpcMethod;
import org.helium.rpc.server.RpcService;
import zconfig.configuration.args.*;

/**
 * work心跳及注册自己路由信息
 * 
 * @author 高磊 gaolei@feinno.com
 */

@RpcService(HAWorkerAgentService.SERVICE_NAME)
public interface HAWorkerAgentService
{
	final String SERVICE_NAME = "HAWorkerAgentService"; 
	/**
	 * 连接到服务器端
	 */
	@RpcMethod("Connect")
	void connect(HAWorkerRegisterArgs args) throws Exception;
	
	
	/**
	 * 注册worker节点
	 */
	@RpcMethod("Register")
	void register(HAWorkerRegisterArgs args) throws Exception;

	/**
	 * 保持worker心跳
	 */
	@RpcMethod("Heartbeat")
	void heartbeat(HAWorkerHeartbeatArgs input) throws Exception;

	/**
	 * 获取监控配置
	 */
	//@RpcMethod("GetMonitorConfig")
	//HAWorkerMonitorConfig getMonitorConfig() throws Exception;
	
	
	/**
	 * 获取配置表
	 */
	@RpcMethod("LoadConfigTable")
	HAConfigTableBuffer loadConfigTable(HAConfigArgs input);

	/**
	 * 获取配置文本
	 */
	@RpcMethod("LoadConfigText")
	HAConfigTextBuffer loadConfigText(HAConfigArgs input);
	
	/**
	 * 
	 * 订阅配置更新
	 */
	@RpcMethod("SubscribeConfig")
	void subscribeConfig(HAConfigArgs input);
	
	/**
	 * 
	 * 更新已订阅的配置版本
	 * @param input
	 */
	@RpcMethod("UpdateConfigVersion")
	void updateConfigVersion(HAConfigArgs input);
}
