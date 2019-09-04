package org.helium.framework.route.zk;

import org.helium.framework.BeanContext;
import org.helium.framework.BeanContextService;
import org.helium.framework.annotations.AdapterTag;
import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.annotations.ServiceSetter;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.framework.entitys.BeanConfiguration;
import org.helium.framework.entitys.FactorGroupNode;
import org.helium.framework.entitys.dashboard.WorkerJson;
import org.helium.framework.route.*;

import org.helium.framework.route.center.BundleReference;
import org.helium.framework.route.center.CentralizedService;
import org.helium.framework.route.center.entity.BundleEndpointNode;
import org.helium.framework.route.center.entity.DummyNode;
import org.helium.framework.route.center.entity.GrayBundleNode;
import org.helium.framework.route.center.entity.VersionedBundleNode;
import org.helium.framework.spi.Bootstrap;
import org.helium.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lei Gao on 8/4/15.
 */
@ServiceImplementation
public class ZkCentralizedService implements CentralizedService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZkCentralizedService.class);

	public static final String ZK_PATH_ROOT = "/Helium";
	public static final String ZK_PATH_SERVERS = "/Helium/Servers";
	public static final String ZK_PATH_BUNDLES = "/Helium/Bundles";
	public static final String ZK_PATH_BUNDLE_ENDPOINTS = "/Helium/BundleEndpoints";
	public static final String ZK_PATH_GRAY_BUNDLES = "/Helium/GrayBundles";
	public static final String CONTEXT_KEY_LOCK = "__LOCK";


	private String zkHosts;
	private ServerEndpoint serverEndpoint;

	private int sessionTimeout = ZooKeeperConnector.ZK_SESSION_TIMEOUT;
	private long refreshThreshold = ZooKeeperConnector.ZK_REFRESH_THRESHOLD;

	private ZooKeeperConnector zk;
	private BeanContextService contextService;

	public void connect(String zkHosts) throws Exception {
		LOGGER.info("try connect ZooKeeper: {}", serverEndpoint.getId());
		zk = new ZooKeeperConnector(zkHosts, sessionTimeout, refreshThreshold);
		zk.connect();
		LOGGER.info("try connect ZooKeeper: {}", serverEndpoint.getId());
	}

	@Override
	public ServerEndpoint getServerEndpoint() {
		return serverEndpoint;
	}

	@Override
	public List<ServerEndpoint> getServerEndpointList() {
		List<ServerEndpoint> serverEndpointList = endpointsByBundle.get(getServerEndpoint().getId().toString());
		return serverEndpointList;
	}

	@Override
	public List<ServerEndpoint> getGrayServerEndpointList() {
		List<ServerEndpoint> serverEndpointList = endpointsByGaryBundle.get(getServerEndpoint().getId().toString());
		return serverEndpointList;
	}

	@Override
	public void register(String serverId, List<ServerUrl> serverUrls, BeanContextService contextService) throws Exception {
		serverEndpoint = new ServerEndpoint();
		serverEndpoint.setId(serverId);
		List<String> urls = CollectionUtils.filter(serverUrls, s -> s.toString());
		serverEndpoint.setUrlList(urls);
		this.contextService = contextService;
		connect(this.zkHosts);

		createBasePaths(serverId);
		zk.createENode(ZK_PATH_SERVERS + "/" + serverId, serverEndpoint, true);
	}

	@Override
	public void syncReferences(BeanContextService service) {
		//
		// 锁定所有在此之前增加的节点
		for (BeanContext bc : service.getBeans()) {
			bc.putAttachment(CONTEXT_KEY_LOCK, true);
		}


		zk.syncNodes(ZK_PATH_BUNDLE_ENDPOINTS, BundleEndpointNode.class, a -> syncBundleEndpoints(a));
		zk.syncNodes(ZK_PATH_BUNDLES, VersionedBundleNode.class, a -> syncBundles(a));
		zk.syncNodes(ZK_PATH_GRAY_BUNDLES, GrayBundleNode.class, a -> syncGrayBundles(a));

	}

	@Override
	public void registerBundle(String bundleName, String bundleVersion, List<BeanConfiguration> beans) throws Exception {
		if (StringUtils.isNullOrEmpty(bundleName)) {
			throw new IllegalArgumentException("<bundle> must have name attribute");
		}
		if (StringUtils.isNullOrEmpty(bundleVersion)) {
			throw new IllegalArgumentException("bundleVersion not support null");
		}
		if (!checkAndCleanExpiredBundle(bundleName, bundleVersion)) {
			VersionedBundleNode node = new VersionedBundleNode();
			node.setBundleName(bundleName);
			node.setBundleVersion(bundleVersion);
			node.setBeans(beans);
			node.setServerEndpoint(serverEndpoint);
			refreshTag(null, beans);
			//
			// sequential节点 /Helium/Bundles/
			zk.createENode(ZK_PATH_BUNDLES + "/" + node.getBundleName() + "#", node, true);
		}

		BundleEndpointNode node2 = new BundleEndpointNode();
		node2.setBundleName(bundleName);
		node2.setBundleVersion(bundleVersion);
		node2.setServerEndpoint(serverEndpoint);

		String epPath = bundleName + "#" + bundleVersion + "#";
		zk.createENode(ZK_PATH_BUNDLE_ENDPOINTS + "/" + epPath, node2, true);
	}

	@Override
	public void registerGrayBundle(String bundleName, String bundleVersion, FactorGroupNode factors, List<BeanConfiguration> beans) throws Exception {
		GrayBundleNode node = new GrayBundleNode();
		node.setBundleName(bundleName);
		node.setBundleVersion(bundleVersion);
		node.setGrayFactors(factors);
		node.setServerEndpoint(serverEndpoint);
		node.setBeans(beans);
		refreshTag(factors, beans);
		zk.createENode(ZK_PATH_GRAY_BUNDLES + "/" + node.getPath(serverEndpoint.getId()), node, false);
	}


	private void refreshTag(FactorGroupNode factors, List<BeanConfiguration> beans) {
		for (BeanConfiguration beanConfiguration : beans) {
			try {
				AdapterTag adapterTag = Class.forName(beanConfiguration.getObject().getClassName()).getAnnotation(AdapterTag.class);
				if (adapterTag == null) {
					continue;
				}
				beanConfiguration.setAdatperTag(adapterTag.name());

				if (factors != null){
					beanConfiguration.setPriority(factors.getPriority());
				}
			} catch (Exception e) {
				LOGGER.error("refreshTag Exception:", e);
			}
		}
	}

	/**
	 * 当已经没有服务器继续引用此Version时，移除此节点
	 *
	 * @param bundleName
	 * @param bundleVersion
	 * @throws Exception
	 */
	@Override
	public void unregisterBundle(String bundleName, String bundleVersion) throws Exception {
		//
		// 移除BundleEndpoint节点，并检查是否可以移除Bundle节点
		final boolean[] canRemoveBundle = {true};
		zk.removeNodeIf(ZK_PATH_BUNDLE_ENDPOINTS, BundleEndpointNode.class, node -> {
			if (bundleName.equals(node.getBundleName()) && bundleVersion.equals(node.getBundleVersion())) {
				if (serverEndpoint.getId().equals(node.getServerEndpoint().getId())) {
					return true;
				} else {
					canRemoveBundle[0] = false;
					return false;
				}
			} else {
				return false;
			}
		});

		if (canRemoveBundle[0]) {
			zk.removeNodeIf(ZK_PATH_BUNDLES, VersionedBundleNode.class, node ->
					bundleName.equals(node.getBundleName()) && bundleVersion.equals(node.getBundleVersion()));
		}
	}

	@Override
	public void unregisterGrayBundle(String bundleName, String bundleVersion) throws Exception {
		zk.removeNodeIf(ZK_PATH_GRAY_BUNDLES, BundleEndpointNode.class,
				node -> bundleName.equals(node.getBundleName()) &&
						bundleVersion.equals(node.getBundleVersion()) &&
						serverEndpoint.getId().equals(node.getServerEndpoint().getId()));
	}



	/**
	 *
	 * @param bundleName
	 * @return
	 */
	@Override
	public ServerRouter subscribeServerRouter(BeanContext bc, String bundleName, String protocol) {
		return new ServerRouter() {
			private int i = 0;
			private KetamaHashLocator<ServerEndpoint> hashLocator;

			@Override
			public int getWeight() {
				synchronized (lock) {
					List<ServerEndpoint> eps = endpointsByBundle.get(bundleName);
					if (eps != null) {
						return eps.size();
					} else {
						return 0;
					}
				}
			}

			@Override
			public BeanContext getBeanContext() {
				return bc;
			}

			@Override
			public ServerUrl pickServer() {
				synchronized (lock) {
					List<ServerEndpoint> eps = getAll();
					if (eps.size() == 0) {
						return null;
					}
					i = (i + 1) % eps.size();
					return eps.get(i).getServerUrl(protocol);
				}
			}

			@Override
			public ServerUrl pickServer(String tag) {
				synchronized (lock) {
					List<ServerEndpoint> eps = getAll();
					if (eps.size() == 0) {
						return null;
					}
					if (hashLocator == null) {
						hashLocator = new KetamaHashLocator(eps);
					} else if (!hashLocator.nodesAreEqual(eps)) {
						hashLocator = new KetamaHashLocator(eps);
					}
					return hashLocator.getPrimary(tag).getServerUrl(protocol);
				}
			}

			@Override
			public boolean hasServer(ServerUrl url) {
				synchronized (lock) {
					List<ServerEndpoint> eps = getAll();
					for (ServerEndpoint ep : eps) {
						if (ep.getServerUrl(protocol).equals(url)) {
							return true;
						}
					}
					return false;
				}
			}

			@Override
			public List<ServerUrl> getAllUrls() {
				List<ServerUrl> list = new ArrayList<>();
				synchronized (lock) {
					List<ServerEndpoint> eps = getAll();
					if (eps != null) {
						for (ServerEndpoint ep : eps) {
							list.add(ep.getServerUrl(protocol));
						}
					}
				}
				return list;
			}

			private List<ServerEndpoint> getAll(){
				List<ServerEndpoint> eps = endpointsByBundle.get(bundleName);
				if (eps == null){
					eps = new ArrayList<>();
				}
				List<ServerEndpoint> epsGray = endpointsByGaryBundle.get(bundleName);
				eps.addAll(epsGray);
				return eps;
			}
		};
	}

	private Map<Combo2<String, String>, ServerRouter> routers = new HashMap<>();

	@Override
	public ServerRouter getServerRouter(String bundleName, String protocol) {
		Combo2<String, String> key = new Combo2<>(bundleName, protocol);
		synchronized (lock) {
			ServerRouter router = routers.get(key);
			if (router == null) {
				router = subscribeServerRouter(null, bundleName, protocol);
				routers.put(key, router);
			}
			return router;
		}
	}

	@Override
	public List<WorkerJson> getWorkers() {
		List<WorkerJson> ret = new ArrayList<>();
		synchronized (lock) {
			for (String key : endpointsByBundle.keys()) {
				for (ServerEndpoint ep : endpointsByBundle.get(key)) {
					WorkerJson j = new WorkerJson();
					j.setBundleName(key);
					j.setServerEndpoints(ep.toString());
					ret.add(j);
				}
			}
		}
		return ret;
	}

	/**
	 * 移除所有已经不存在Endpoints
	 *
	 * @param bundleName
	 * @param bundleVersion
	 * @return
	 * @throws Exception
	 */
	public boolean checkAndCleanExpiredBundle(String bundleName, String bundleVersion) throws Exception {
		//
		// 检索该Bundle已经存在的ZkEndpoint节点
		Map<String, BundleEndpointNode> eps = new HashMap<>();
		List<BundleEndpointNode> endpoints = zk.getNodes(ZK_PATH_BUNDLE_ENDPOINTS, BundleEndpointNode.class);
		for (BundleEndpointNode ep : endpoints) {
			if (bundleName.equals(ep.getBundleName())) {
				eps.put(ep.getEndpointPath(), ep);
			}
		}

		//
		// 移除所有已经没有Ep的同名节点
		final boolean[] registered = {false};

		//
		// 并且判断有没有同样版本
		zk.removeNodeIf(ZK_PATH_BUNDLES, VersionedBundleNode.class, b -> {
			if (bundleName.equals(b.getBundleName())) {
				if (b.getBundleVersion().equals(bundleVersion)) {
					registered[0] = true;
				}
				if (!eps.containsKey(b.getEndpointPath())) {
					return true;
				}
			}
			return false;
		});

		return false;
	}

	/**
	 * 内部测试方法，清除所有灰度节点
	 */
	public void cleanBundles() throws Exception {
		zk.removeNodeIf(ZK_PATH_BUNDLES, VersionedBundleNode.class, a -> true);
	}

	private void createBasePaths(String serverId) throws Exception {
		zk.createNode(ZK_PATH_ROOT, new DummyNode(serverId), false);
		zk.createNode(ZK_PATH_SERVERS, new DummyNode(serverId), false);
		zk.createNode(ZK_PATH_BUNDLES, new DummyNode(serverId), false);
		zk.createNode(ZK_PATH_BUNDLE_ENDPOINTS, new DummyNode(serverId), false);
		zk.createNode(ZK_PATH_GRAY_BUNDLES, new DummyNode(serverId), false);
	}


	/**
	 * 节点同步信息
	 */
	private Object lock = new Object();
	private Map<String, BundleEndpointNode> endpoints = new HashMap<>();
	private Map<String, VersionedBundleNode> bundles = new HashMap<>();
	private Map<String, GrayBundleNode> grayBundles = new HashMap<>();
	private Map<String, BundleReference> bundleRefs = new HashMap<>();
	private DictionaryList<String, ServerEndpoint> endpointsByBundle = new DictionaryList<>();
	private DictionaryList<String, ServerEndpoint> endpointsByGaryBundle = new DictionaryList<>();


	private void syncBundles(Map<String, VersionedBundleNode> map) {
		LOGGER.info("syncBundles map.size={}", map.size());

		synchronized (lock) {
			//1.同步常驻节点
			MapComparator.compare(bundles, map).forEach(m -> {
				try {
					VersionedBundleNode node = m.getValue();
					BundleReference ref = getBundleReference(node.getBundleName());
					switch (m.getModifyType()) {
						case INSERT:
							LOGGER.info("syncBundle INSERT bundle={} version={}", node.getBundleName(), node.getBundleVersion());
							ref.addVersion(m.getValue());
							break;
						case UPDATE:
							ref.addVersion(m.getValue());
							LOGGER.error("UpdateAction not support: {}" + node);
							break;
						case DELETE:
							LOGGER.info("syncBundle DELETE bundle={} version={}", node.getBundleName(), node.getBundleVersion());
							ref.removeVersion(m.getValue());
							break;
					}
				} catch (Exception ex) {
					LOGGER.error("syncBundle failed id=" + m.getValue().getBundleName() + ": {}", ex);
				}
			});
			bundles = map;

		}
	}

	private void syncBundleEndpoints(Map<String, BundleEndpointNode> map) {
		LOGGER.info("syncBundleEndpoints map.size={}", map.size());
		synchronized (lock) {
			//1.同步常驻节点
			DictionaryList dl = new DictionaryList();
			map.forEach((k, v) -> {
				dl.put(v.getBundleName(), v.getServerEndpoint());
			});
			endpointsByBundle = dl;
			endpoints = map;

			//2. 更新本机远程service依赖
			Bootstrap.INSTANCE.getBundleManager().updateBundles(Bootstrap.INSTANCE);
		}

	}

	private void syncGrayBundles(Map<String, GrayBundleNode> map) {
		LOGGER.info("sunGrayBundles map.size{}", map.size());
		synchronized (lock) {
			//1.方法同步
			MapComparator.compare(grayBundles, map).forEach(m -> {
				GrayBundleNode node = m.getValue();

				BundleReference ref = getBundleReference(node.getBundleName());

				switch (m.getModifyType()) {
					case INSERT:
						LOGGER.info("syncGrayBundle INSERT bundle={} ep={}", node.getBundleName(), node.getServerEndpoint());
						ref.addGrayBundle(node);
						break;
					case UPDATE:
						LOGGER.error("updateGrayBundle is not Support: {}", node);
						break;
					case DELETE:
						LOGGER.info("syncGrayBundle DELETE bundle={} ep={}", node.getBundleName(), node.getServerEndpoint());
						ref.removeGrayBundle(node);
						break;
				}
			});
			//2. 同步灰度节点
			DictionaryList dl = new DictionaryList();
			map.forEach((k, v) -> {
				dl.put(v.getBundleName(), v.getServerEndpoint());
			});
			endpointsByGaryBundle = dl;
			grayBundles = map;

			//3. 更新本机远程service依赖

			Bootstrap.INSTANCE.getBundleManager().updateBundles(Bootstrap.INSTANCE);
		}
	}

	private BundleReference getBundleReference(String bundleName) {
		BundleReference ref = bundleRefs.get(bundleName);
		if (ref == null) {
			ref = new BundleReference(bundleName, contextService);
			bundleRefs.put(bundleName, ref);
		}
		return ref;
	}
}

