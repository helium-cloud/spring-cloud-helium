package zconfig.configuration;


import org.helium.rpc.server.RpcMethod;
import org.helium.rpc.server.RpcService;
import zconfig.configuration.args.HAConfigArgs;

@RpcService("HAWorkerAgentCallbackService")
public interface HAWorkerAgentCallbackService
{	
	/**
	 * 
	 * 通知配置过期
	 */
	@RpcMethod("NotifyConfigExpired")
	public void notifyConfigExpired(HAConfigArgs args);
}
