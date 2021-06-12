//package org.helium.cloud.task.rpc;
//
//
//import org.apache.dubbo.common.URL;
//import org.apache.dubbo.config.ApplicationConfig;
//import org.apache.dubbo.config.ReferenceConfig;
//import org.apache.dubbo.config.RegistryConfig;
//import org.apache.dubbo.config.ServiceConfig;
//import org.helium.cloud.common.utils.SpringContextUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// *
// *
// */
//public class TaskInvokerFactoryCenter implements TaskInvokerFactory {
//
//    public  String version = "1.0.0";
//	public  String registryUrl = "";
//	public  TaskInvoker taskInvoker = null;
//    private static final Logger LOGGER = LoggerFactory.getLogger(TaskInvokerFactoryCenter.class);
//
//
//	public TaskInvokerFactoryCenter(String registryUrl){
//		this.registryUrl = registryUrl;
//		try {
//			initServiceConfig();
//		} catch (Exception e){
//			LOGGER.error("TaskInvokerFactoryCenter exception:", e);
//		}
//
//	}
//
//	public void initServiceConfig(){
//
//
//		RegistryConfig registry = new RegistryConfig();
//		URL regUrl = URL.valueOf(registryUrl);
//		registry.setAddress(regUrl.getAddress());
//		registry.setProtocol(regUrl.getProtocol());
//
//		ServiceConfig<TaskInvoker> service = new ServiceConfig<TaskInvoker>();
//		service.setInterface(TaskInvoker.class);
//		service.setRef(new TaskInvokerImpl());
//		service.setVersion(version);
//		ApplicationConfig applicationConfig = null;
//		try {
//			applicationConfig = SpringContextUtil.getBean(ApplicationConfig.class);
//		} catch (Exception e){
//			applicationConfig = new ApplicationConfig();
//			applicationConfig.setName("DtTaskConsumer");
//		}
//		service.setApplication(applicationConfig);
//		service.setRegistry(registry);
//		service.setGroup(applicationConfig.getName());
//		service.export();
//	}
//
//	/**
//	 * random=org.apache.dubbo.rpc.cluster.loadbalance.RandomLoadBalance
//	 * roundrobin=org.apache.dubbo.rpc.cluster.loadbalance.RoundRobinLoadBalance
//	 * leastactive=org.apache.dubbo.rpc.cluster.loadbalance.LeastActiveLoadBalance
//	 * consistenthash=org.apache.dubbo.rpc.cluster.loadbalance.ConsistentHashLoadBalance
//	 *
//	 * @return
//	 */
//    public TaskInvoker checkTaskInvoker(){
//    	//TODO 本机的话就不走rpc
//        if (taskInvoker == null){
//            synchronized (TaskInvokerFactoryCenter.class){
//            	if (taskInvoker == null){
//					RegistryConfig registry = new RegistryConfig();
//					URL regUrl = URL.valueOf(registryUrl);
//					registry.setAddress(regUrl.getAddress());
//					registry.setProtocol(regUrl.getProtocol());
//					ReferenceConfig<TaskInvoker> referenceConfig = new ReferenceConfig<>();
//					referenceConfig.setRegistry(registry);
//					referenceConfig.setVersion(version);
//					//采用一致性hash
//					referenceConfig.setLoadbalance("consistenthash");
//					referenceConfig.setProtocol("dubbo");
//					referenceConfig.setInterface(TaskInvoker.class);
//					referenceConfig.setCheck(false);
//					referenceConfig.setInjvm(false);
//					ApplicationConfig applicationConfig = null;
//					try {
//						applicationConfig = SpringContextUtil.getBean(ApplicationConfig.class);
//					} catch (Exception e){
//						applicationConfig = new ApplicationConfig();
//						applicationConfig.setName("DtTaskConsumer");
//					}
//					referenceConfig.setGroup(applicationConfig.getName());
//					taskInvoker = referenceConfig.get();
//				}
//            }
//        }
//        return taskInvoker;
//
//    }
//
//
//
//	@Override
//	public TaskInvoker getInvoker() {
//		checkTaskInvoker();
//		return taskInvoker;
//	}
//}
