package org.helium.framework.route.center;

import org.helium.framework.BeanContextState;
import org.helium.framework.BeanType;
import org.helium.framework.configuration.BeanContextProvider;
import org.helium.framework.entitys.BeanConfiguration;
import org.helium.framework.module.ModuleContext;
import org.helium.framework.route.ServerEndpoint;
import org.helium.framework.route.ServerUrl;
import org.helium.framework.route.abtest.Factor;
import org.helium.framework.route.abtest.FactorGroup;
import org.helium.framework.route.center.entity.VersionedBundleNode;
import org.helium.framework.service.ServiceContext;
import org.helium.framework.service.ServiceMatchResult;
import org.helium.framework.service.ServiceMatchResults;
import org.helium.framework.spi.ServiceReference;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理灰度及不同版本下的同步
 * 当前的处理方式存在的问题：
 * <p>
 * Created by Coral on 8/6/15.
 */
public class ServiceReferenceCombo extends ServiceReference implements ServiceContext {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceReferenceCombo.class);

	private String protocol;
	private List<RefNode> primaries;
	private List<RefNode> experiments;

	public ServiceReferenceCombo(BeanConfiguration bc, BeanContextProvider contextProvider) {
		super(bc, contextProvider);
		if (bc.getServletMappings() == null) {
			this.protocol = "rpc";
		} else {
			this.protocol = bc.getServletMappings().getProtocol();
		}

		primaries = new ArrayList<>();
		experiments = new ArrayList<>();
	}

	@Override
	public String getProtocol() {
		return protocol;
	}

	@Override
	public String getAdapterTag() {
		if (getConfiguration() == null) {
			return null;
		}
		return getConfiguration().getAdatperTag();
	}

	@Override
	public ServiceMatchResults matchRequest(ModuleContext ctx, Object... args) {
		ServiceMatchResults results = new ServiceMatchResults();
		synchronized (this) {
			//
			// 如果可能进行灰度记录的匹配则，将匹配的灰度节点加入结果
			int copyNum = 0;
			if (ctx != null) {
				for (RefNode ref : experiments) {
					//匹配灰度节点
					if (ref.factor.apply(ctx)) {
						ServiceMatchResult serviceMatchResult = new ServiceMatchResult();
						serviceMatchResult.setServerUrl(ref.getServerUrl());
						serviceMatchResult.setDuplicate(ref.factor.duplicate());
						serviceMatchResult.setBeanIdentity(getId());
						serviceMatchResult.setInterfaceClass(getConfiguration().getInterfaceClazz());
						if (ref.factor instanceof FactorGroup) {
							FactorGroup factorGroup = (FactorGroup) ref.factor;
							if (factorGroup.getFactors() == null || factorGroup.getFactors().size() == 0) {
								serviceMatchResult.setAll(true);
							}
						}
						serviceMatchResult.setPriority(getConfiguration().getPriority());
						results.addResult(serviceMatchResult);
						//针对复制节点计数
						if (ref.factor.duplicate()) {
							copyNum++;
						}

					}
				}
			}

			//过滤灰度节点
			if (results.getResults() != null && results.getResults().size() > 0) {
				List<ServiceMatchResult> resultList = results.getResults();
				//除去全量灰度节点
				for (int i = 0; i < resultList.size() && (resultList.size() > 1);) {
					if (resultList.get(i).isAll()) {
						resultList.remove(i);
						continue;
					}
					i++;
				}
				//若灰度节点全为复制节点需走全量节点
				if (copyNum < results.getResults().size()) {
					return results;
				}
			}
			//
			//增加全量节点
			for (RefNode ref : primaries) {
				ServiceMatchResult serviceMatchResult = new ServiceMatchResult();
				serviceMatchResult.setBeanIdentity(getId());
				serviceMatchResult.setInterfaceClass(getConfiguration().getInterfaceClazz());
				serviceMatchResult.setServerUrl(ref.getServerUrl());
				serviceMatchResult.setPriority(getConfiguration().getPriority());
				results.addResult(serviceMatchResult);

			}
		}
		return results;
	}

	/**
	 * 增加主版本
	 *
	 * @param version
	 * @param bc
	 */
	public void addPrimary(String version, BeanConfiguration bc, String bundleName, VersionedBundleNode bundle) {
		try {
			if (bundle.getServerEndpoint() == null){
				LOGGER.info("Primary Support:{}", bc.getId());
				return;
			}
			synchronized (this) {
				if (bc.getInterfaceClazz() == null && !StringUtils.isNullOrEmpty(bc.getInterfaceType())){
					bc.setInterfaceClazz(Class.forName(bc.getInterfaceType()));
				}
				RefNode node = new RefNode(version, bundle.getServerEndpoint().getServerUrl("rpc"));
				node.serverId = bundle.getServerEndpoint().getId();
				LOGGER.info("addPrimary:{}:{}", node.serverId, node.serverUrl.getUrl());
				primaries.add(node);
				setConfiguration(bc);

			}
		} catch (ClassNotFoundException e){
			LOGGER.debug("addPrimary Not Require:{}", bc.getId());
		} catch (Exception e){
			LOGGER.error("addPrimary Exception:{}:{}", bc.getId(), e.getMessage());
		}

	}

	/**
	 * 移除主版本
	 *
	 * @param version
	 */
	public void removePrimary(String version) {
		synchronized (this) {
			primaries.removeIf(n -> version.equals(n.version));
		}
	}

	public void addExperiment(String version, BeanConfiguration bc, Factor factor, ServerEndpoint server){

		try {
			ServerUrl url = server.getServerUrl(protocol);
			if (bc.getInterfaceClazz() == null && !StringUtils.isNullOrEmpty(bc.getInterfaceType())){
				bc.setInterfaceClazz(Class.forName(bc.getInterfaceType()));
			}
			RefNode ref = new RefNode(version, url);
			ref.factor = factor;
			ref.serverId = server.getId();
			synchronized (this) {
				experiments.add(ref);
				LOGGER.info("addExperiment:{}:{}", ref.serverId, ref.serverUrl.getUrl());
			}
		} catch (ClassNotFoundException e){
			LOGGER.debug("addExperiment Not Require:{}", bc.getId());
		} catch (Exception e){
			LOGGER.error("addExperiment Exception:{}-{}", bc.getId(), e.getMessage());
		}


	}

	public void removeExperiment(String version, ServerEndpoint server) {
		synchronized (this) {
			experiments.removeIf(ref -> ref.version.equals(version) && ref.serverId.equals(server.getId()));
		}
	}

	public boolean isEmpty() {
		return primaries.isEmpty() && experiments.isEmpty();
	}


	@Override
	public boolean isLocal() {
		return false;
	}


	@Override
	public BeanType getType() {
		return BeanType.SERVICE;
	}

	@Override
	public BeanContextState getState() {
		return BeanContextState.RESOLVED;
	}


	private Map<String, Object> attachments = new HashMap<>();

	@Override
	public Object putAttachment(String key, Object value) {
		synchronized (this) {
			return attachments.put(key, value);
		}
	}

	@Override
	public Object getAttachment(String key) {
		synchronized (this) {
			return attachments.get(key);
		}
	}


	public List<RefNode> getPrimaries() {
		return primaries;
	}

	public void setPrimaries(List<RefNode> primaries) {
		this.primaries = primaries;
	}

	public List<RefNode> getExperiments() {
		return experiments;
	}

	public void setExperiments(List<RefNode> experiments) {
		this.experiments = experiments;
	}

	public Map<String, Object> getAttachments() {
		return attachments;
	}

	public void setAttachments(Map<String, Object> attachments) {
		this.attachments = attachments;
	}

	/**
	 * 提供给GetBeansServlet读取服务信息使用
	 *
	 * @return
	 */
	public List<ServerUrl> getServiceUrls() {
		List<ServerUrl> urls = new ArrayList<>();
		for (RefNode expr : experiments) {
			urls.add(expr.getServerUrl());
		}

		for (RefNode primary : primaries) {
			urls.add(primary.getServerUrl());
		}
		return urls;
	}

	private static class RefNode {
		private String version;
		private Factor factor;
		private ServerUrl serverUrl;
		private String serverId;

		RefNode(String version, ServerUrl serverUrl) {
			this.version = version;
			this.serverUrl = serverUrl;
		}

		public ServerUrl getServerUrl() {
			return serverUrl;
		}
	}

}