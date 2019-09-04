package org.helium.framework.route.zk;

import com.feinno.superpojo.SuperPojo;

import org.apache.zookeeper.*;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.helium.framework.route.center.CentralizedMonitor;
import org.helium.framework.spi.ObjectCreator;

import org.helium.util.Action;
import org.helium.util.CollectionUtils;
import org.helium.util.Combo3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;

/**
 * Created by Lei Gao on 8/4/15.
 */
public class ZooKeeperConnector {
	public static final int ZK_SESSION_TIMEOUT = 10 * 2000; // 好像是zk
	public static final int ZK_REFRESH_THRESHOLD = 2 * 1000;

	private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperConnector.class);

	private String zkHosts;
	private int sessionTimeout;
	private long refreshThreshold;

	private ZooKeeper zk;
	private ZooKeeperDataLoader dataLoader;

	private List<ZNode> enodes;
	private Map<String, CentralizedMonitor> monitors;

	//扩展的节点,用于重连后的节点,watcher的再生
	private Map<String, Combo3<String, Watcher, Stat>> extraMonitors;
	private List<ZNode> extraEnodes;

	public ZooKeeperConnector(String zkHosts) {
		this(zkHosts, ZK_SESSION_TIMEOUT, ZK_REFRESH_THRESHOLD);
	}

	public ZooKeeperConnector(String zkHosts, int sessionTimeout, long refreshThreshold) {
		this.zkHosts = zkHosts;
		this.sessionTimeout = sessionTimeout;
		this.refreshThreshold = refreshThreshold;

		enodes = new ArrayList<>();
		monitors = new HashMap<>();

		//非helium,用于基于helium扩展的组件,重连时恢复数据
		extraEnodes = new ArrayList<>();
		extraMonitors = new HashMap<>();
	}

	public void connect() throws Exception {
		zk = new ZooKeeper(zkHosts, sessionTimeout, new SessionWatcher());
		dataLoader = new ZooKeeperDataLoader(refreshThreshold, zk);
	}

	public boolean existsNode(String path) throws Exception {
		Stat stat = zk.exists(path, false);
		return stat != null;
	}

	public ZooKeeper getZookeeper(){
		return zk;
	}

	public <E extends SuperPojo> void removeNodeIf(String path, Class<E> clazz, Predicate<E> func) throws Exception {
		Stat stat = new Stat();
		for (String child: zk.getChildren(path, false)) {
			String childPath = path + "/" + child;
			byte[] buffer = zk.getData(childPath, false, stat);
			E value = (E)clazz.newInstance();
			value.parsePbFrom(buffer);
			if (func.test(value)) {
				zk.delete(childPath, stat.getVersion());
			}
		}
	}

	public <E extends SuperPojo> List<E> getNodes(String path, Class<E> clazz) throws Exception {
		Stat stat = new Stat();
		List<E> results = new ArrayList<>();
		for (String child: zk.getChildren(path, false)) {
			String childPath = path + "/" + child;
			byte[] buffer = zk.getData(childPath, false, stat);
			E value = (E)clazz.newInstance();
			value.parsePbFrom(buffer);
			results.add(value);
		}
		return results;
	}

	public <E extends SuperPojo> E getNode(String path, Class<E> clazz) throws Exception {
		Stat stat = new Stat();
		byte[] buffer = zk.getData(path, false, stat);
		E value = (E)clazz.newInstance();
		value.parsePbFrom(buffer);
		return value;
	}

	public <E extends SuperPojo> String createENode(String path, E data, boolean sequential) throws Exception {
		ZNode node = new ZNode();
		node.path = path;
		node.data = data.toPbByteArray();
		node.mode = sequential ? CreateMode.EPHEMERAL_SEQUENTIAL : CreateMode.EPHEMERAL;
		synchronized (this) {
			enodes.add(node);
		}
		return zk.create(node.path, node.data, Ids.OPEN_ACL_UNSAFE, node.mode);
	}


	/**
	 * 扩展创建临时节点接口,
	 * 当外部服务想要扩展zk处理时,
	 * helium的默认zk watcher能够重连的时候
	 * 创建这些临时节点
	 *
	 * @param path
	 * @param data
	 * @param sequential
	 * @return
	 * @throws Exception
	 */
	public String createExtraENode(String path, byte[] data, boolean sequential) throws Exception {
		ZNode node = new ZNode();
		node.path = path;
		node.data = data;
		node.mode = sequential ? CreateMode.EPHEMERAL_SEQUENTIAL : CreateMode.EPHEMERAL;
		synchronized (this) {
			extraEnodes.add(node);
		}
		return zk.create(node.path, node.data, Ids.OPEN_ACL_UNSAFE, node.mode);
	}


	/**
	 * 增加额外的基于zk的Watcher
	 *
	 * @param nodePath
	 * @param watcher
	 * @param stat
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void storeExtraMonitor(String nodePath, Watcher watcher, Stat stat) throws KeeperException, InterruptedException {
		extraMonitors.put(nodePath, new Combo3<>(nodePath, watcher, stat));
	}

	public <E extends SuperPojo> void createNode(String path, E data, boolean sequential) throws Exception {
		//TODO: use lockNode(EPHEMERAL in /Locks/lock_pathWithEscape), for cluster server
		if (!existsNode(path)) {
			CreateMode mode = sequential ? CreateMode.PERSISTENT_SEQUENTIAL : CreateMode.PERSISTENT;
			zk.create(path, data.toPbByteArray(), Ids.OPEN_ACL_UNSAFE, mode);
		}
	}

	public <E extends SuperPojo> void syncNodes(String path, Class<E> clazz, Action<Map<String, E>> callback) {
		CentralizedMonitor monitor = new CentralizedMonitor() {
			@Override
			public void update(Map<String, byte[]> datas) {
				Map<String, E> map = new HashMap<>();
				for (Entry<String, byte[]> e : datas.entrySet()) {
					E value = (E) ObjectCreator.createObject(clazz);
					value.parsePbFrom(e.getValue());
					map.put(e.getKey(), value);
				}
				callback.run(map);
			}
		};
		synchronized (this) {
			monitors.put(path, monitor);
		}
		dataLoader.load(path, monitor);
	}

	private void reconnect() throws Exception {
		try {
			zk.close();
			dataLoader.shutdown();
		} catch (Exception ex) {
			LOGGER.error("free before reconnect() failed:{}", ex);
		}
		zk = new ZooKeeper(zkHosts, sessionTimeout, new SessionWatcher());
		dataLoader = new ZooKeeperDataLoader(refreshThreshold, zk);
		restoreENodes();
		restoreMonitors();

		//恢复节点和watcher
		restoreExtraENodes();
		restoreExtraMonitors();
	}

	/**
	 * 恢复非helium,扩展的临时节点
	 */
	private void restoreExtraENodes() {
		try {
			List<ZNode> nodes;
			synchronized (this) {
				nodes = CollectionUtils.filter(extraEnodes, n -> n);
			}
			for (ZNode node : nodes) {
				if (!this.existsNode(node.path)) {
					zk.create(node.path, node.data, Ids.OPEN_ACL_UNSAFE, node.mode);
				}
			}
		} catch (Exception e) {
			LOGGER.error("restoreExtraENodes error!", e);
		}
	}

	/**
	 * 恢复非helium,扩展的Monitor(Watcher)
	 */
	private void restoreExtraMonitors() {
		try {
			Set<String> keys = extraMonitors.keySet();
			Iterator<String> iterator = keys.iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				Combo3<String, Watcher, Stat> value = extraMonitors.get(key);
				zk.getData(value.getV1(), value.getV2(), value.getV3());
			}
		}catch (Exception e) {
			LOGGER.error("restoreExtraMonitors error!", e);
		}
	}

	private void restoreENodes() throws Exception {
		List<ZNode> nodes;
		synchronized (this) {
			nodes = CollectionUtils.filter(enodes, n -> n);
		}
		for (ZNode node : nodes) {
			zk.create(node.path, node.data, Ids.OPEN_ACL_UNSAFE, node.mode);
		}
	}

	private void restoreMonitors() throws Exception {
		List<Entry<String, CentralizedMonitor>> list;
		synchronized (this) {
			list = CollectionUtils.cloneEntrys(monitors);
		}
		for (Entry<String, CentralizedMonitor> e : list) {
			dataLoader.load(e.getKey(), e.getValue());
		}
	}

	/**
	 * internal class
	 */
	private static class ZNode {
		String path;
		byte[] data;
		CreateMode mode;
	}

	private class SessionWatcher implements Watcher {
		// single thread (zooKeeper eventThread)
		@Override
		public void process(WatchedEvent event) {
			if (event.getType() == Event.EventType.None) {
				switch (event.getState()) {
					case SyncConnected:
						LOGGER.info("zk Connected");
						break;
					case Disconnected:
						LOGGER.warn("zk disconnected, url {}, passively waiting SyncConnected", zkHosts);
						break;
					case Expired:
					case AuthFailed:
						// It's all over, reconnect
						LOGGER.warn("zk session timeout, re-establishing connection, url {}", zkHosts);
						boolean inited = false;
						while (!inited) { // never give up until re-initialized?
							try {
								ZooKeeperConnector.this.reconnect();
								inited = true;
							} catch (Exception e) {
								LOGGER.warn("re-init failed", e);
								try {
									Thread.sleep(2 * 1000); // TODO
								} catch (InterruptedException ie) {
									LOGGER.warn("damn, I am sleeping", ie);
								}
							}
						}
						break;
					default:
						LOGGER.warn("what? we got an unexpected event. [{}]", event.getState());
				}
			}
		}
	}
}
