package org.helium.framework.service;

import org.helium.framework.BeanContextService;
import org.helium.framework.BeanIdentity;
import org.helium.framework.module.ModuleContext;
import org.helium.framework.route.center.ServiceReferenceCombo;
import org.helium.framework.spi.ServiceReference;
import org.helium.rpc.channel.tcp.RpcTcpEndpoint;
import org.helium.rpc.client.RpcProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 用于做服务路由，支持全量业务负载和灰度业务路由
 * beans全量业务注册
 * beansForRead相对应业务注册
 */
public class ServiceRouter<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRouter.class);

	private TreeMap<String, ServiceReferenceCombo> beansForRead = new TreeMap<>();
	private String adapterTag = "";

	private ServiceNotifyHandler serviceNotifyHandler = null;

	/**
	 * 初始并监听原始节点
	 * @param contextService
	 * @param adapterTag
	 */
	public ServiceRouter(BeanContextService contextService, String adapterTag, ServiceNotifyHandler serviceNotifyHandler) {
		this.adapterTag = adapterTag;
		this.serviceNotifyHandler = serviceNotifyHandler;
		contextService.syncBeans(
				(sender, modification) -> {
					switch (modification.getAction()) {
						case INSERT:
						case UPDATE:
						case DELETE:
							//此处为service路由
							if (modification.getBeanContext() instanceof ServiceReferenceCombo){
								if (LOGGER.isInfoEnabled()){
									LOGGER.info("{} service:{}", modification.getAction().toString(), modification.getBeanContext().getId());
								}
								updateBean((ServiceReferenceCombo) modification.getBeanContext());
								break;
							}

					}
				},
				bc -> {
					return true;
				}
		);

	}

	/**
	 * 返回结果, 使用传入的过滤器参与计算
	 * 1.灰度匹配
	 * 	 1.1 匹配灰度路由
	 * 	 1.2 删除全量灰度（遍历一次）
	 * 	 1.3 若全为复制节点为全量路由增加返回
	 * 	 1.4 返回灰度节点
	 * 2.二次业务过滤
	 * 	2.1 判空处理
	 * 	2.2 塞选复制节点
	 *
	 * 3.一致性hash
	 *
	 * 4.创建proxy代理
	 * @param ctx
	 * @param args
	 * @return
	 */
	public ServiceMatchResults match(ModuleContext ctx,String key, int priority, Object... args) {
		//1.结果集灰度判断
		ServiceMatchResults resultsMatch = new ServiceMatchResults();
		for(Map.Entry<String, ServiceReferenceCombo> beanMap:beansForRead.entrySet()){
			ServiceReferenceCombo bean = beanMap.getValue();
			ServiceMatchResults matchResults = bean.matchRequest(ctx, args);
			resultsMatch.addResults(matchResults);
		}
		//2.二次业务过滤
		List<ServiceMatchResult<T>> resultMathList = resultsMatch.getResults();
		if (resultMathList == null || resultMathList.size() == 0){
			return resultsMatch;
		}
		ServiceMatchResults<T> resultsWork = new ServiceMatchResults();
		List<ServiceMatchResult<T>> resultsNoDuplicate = new ArrayList<>();
		int curPriority = -1;
		resultMathList.sort(new ServiceMatchCompare());
		for (ServiceMatchResult<T> result: resultMathList) {

			if (result.isDuplicate()){
				resultsWork.addResult(result);
			} else {
				if (result.getPriority() <= priority){
					//该路由已经匹配
					continue;
				} else if (curPriority == -1){
					//第一个结果
					curPriority = result.getPriority();
					resultsNoDuplicate.add(result);
				} else if(curPriority == result.getPriority()){
					//后续结果
					resultsNoDuplicate.add(result);
				} else {
					continue;
				}
			}

		}
		//3.塞选一致hash节点
		if (resultsNoDuplicate.size() > 0){
			HashedServiceConsistent<ServiceMatchResult<T>>  serviceList = new HashedServiceConsistent(1000, resultsNoDuplicate);
			resultsWork.addResult(serviceList.get(key));
		}


		for (ServiceMatchResult<T> result : resultsWork.getResults()) {
			try {
				BeanIdentity beanIdentity = result.getBeanIdentity();
				String serviceName = ServiceReference.getRpcServiceName(beanIdentity);
				Class serviceInterface = result.getInterfaceClass();
				T transparentProxy = (T) RpcProxyFactory.getTransparentProxy(serviceName, serviceInterface, () -> {
					return RpcTcpEndpoint.parse(result.getServerUrl().getUrl());
				});
				result.setProxy(transparentProxy);
			} catch (Exception e) {
				LOGGER.error("create bean Exception:{}", result.getServerUrl().getUrl(), e);
			}

		}
		return resultsWork;
	}


	private void updateBean(ServiceReferenceCombo bean) {
		synchronized (this) {
			if (adapterTag.equals(bean.getAdapterTag())) {
				//step1 删除无效bean
				if (bean.isEmpty()){
					if (LOGGER.isInfoEnabled()){
						LOGGER.info("beansForRead.remove:{}", bean.getId().toString());
					}
					beansForRead.remove(bean.getId().toString());
					return;
				}

				//step2 更新已有bean
				ServiceReferenceCombo oldBean = beansForRead.get(bean.getId().toString());
				if (oldBean != null){
					if (LOGGER.isInfoEnabled()){
						LOGGER.info("beansForRead.update:{}", bean.getId().toString());
					}
					//灰度节点及临时节点
					if (oldBean.getPrimaries().size() != bean.getPrimaries().size()){
						if (serviceNotifyHandler != null){
							// TODO 待处理
							serviceNotifyHandler.notifyNode();
						}
					}
					if (oldBean.getAttachments().size() != bean.getAttachments().size()){
						if (serviceNotifyHandler != null){
							// TODO 待处理
						}

					}
					beansForRead.replace(bean.getId().toString(), bean);
					return;
				}

				//step3 添加bean
				beansForRead.put(bean.getId().toString(), bean);
			}

		}
	}
}
