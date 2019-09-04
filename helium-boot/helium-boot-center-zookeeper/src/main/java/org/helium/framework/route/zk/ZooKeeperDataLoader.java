package org.helium.framework.route.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.helium.framework.route.center.CentralizedMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * FROM feinno-app-engine create by Aug 20, 2011
 *
 * @author 高磊 gaolei@feinno.com
 */
public class  ZooKeeperDataLoader implements Runnable, Watcher {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperDataLoader.class);

	private LinkedBlockingQueue<String> notificationQueue = new LinkedBlockingQueue<>();
	private Map<String, CentralizedMonitor> monitors = new HashMap<>();
	private Set<String> notified = new java.util.HashSet<>();    // 不会被其他线程使用

	private long updateInterval;
	private Thread notificationRecorder;
	private ZooKeeper zk;

	private volatile boolean shutdown = false;

	public ZooKeeperDataLoader(long updateInterval, ZooKeeper zk) {
		this.updateInterval = updateInterval;
		this.zk = zk;
		this.notificationRecorder = new Thread(this, "DataLoader");
		notificationRecorder.start();
	}

	public void load(String node, CentralizedMonitor dm) {
		LOGGER.debug("loading node {}", node);
		monitors.put(node, dm);
		LOGGER.debug("monitors put {}-{}", node, dm);
		load(node);
	}

	public void shutdown() {
		this.shutdown = true;
		notificationRecorder.interrupt();
	}

	private void load(String node) {
		LOGGER.debug("loading and watching [{}] node", node);
		Stat stat = new Stat();
		try {
			List<String> children = zk.getChildren(node, this);
			Map<String, byte[]> datas = new HashMap<String, byte[]>();

			LOGGER.debug("got list");
			for (String child : children) {
				LOGGER.debug("node name : {}", child);
				try {
					byte[] data = zk.getData(node + "/" + child, this, stat);
					datas.put(child, data);
					LOGGER.debug("node data len : {}", data.length);
				} catch (Exception e) {
					LOGGER.warn("exception occurred (ignored) during fetching node[{}] data, ignored.\n Exception:{}",
							child, e);
				}
			}

			CentralizedMonitor dm = this.monitors.get(node);
			if (dm != null) {
				LOGGER.debug("updating..");
				dm.update(datas);
			} else {
				LOGGER.debug("NO DataMonitor found");
			}
		} catch (Exception e) {
			// add this node to watch list
			handleThis(node);
			LOGGER.warn("exception occurred  during *getChildren*.", e);
		}
	}

	private void drainAll() {
		for (String node : notified) {
			load(node);
		}
		notified.clear();
	}

	private void recordNodeNotification(String node) {
		notified.add(node);
	}

	private boolean hasNotifications() {
		return notified.size() > 0;
	}

	private void handleThis(String node) {
		try {
			notificationQueue.put(node);
		} catch (InterruptedException e) {
			LOGGER.warn("notificationQueue interrupted.");
		}
	}

	/**
	 * 在updateInterval..
	 */
	@Override
	public void run() {
		LOGGER.debug("zk data loader started.");
		String node;
		while (!shutdown) {
			try {
				if (!hasNotifications()) {
					// 没有累积事件，死等
					LOGGER.debug("waiting...(no timeout)");
					node = notificationQueue.take();
					recordNodeNotification(node);
				} else {
					// 有累积事件
					node = notificationQueue.poll(updateInterval, TimeUnit.MILLISECONDS);
					if (node == null) {
						// 指定时间间隔内，无数据更新，视为稳定
						LOGGER.debug("got notification (from timer), stabilized, reloading nodes");
						drainAll();
					} else {
						// 记录变化的节点，推迟数据加载
						LOGGER.debug("got notification (node : {}).", node);
						recordNodeNotification(node);
					}
				}
			} catch (InterruptedException e) {
				LOGGER.warn("queue polling operation interrupted.", e);
				// break; // never
			}
		}
		LOGGER.info("loader thread quit.");
	}

	String toParentNode(String node) {
		String arg = node;
		if (node.endsWith("/")) {
			arg = node.substring(0, node.length() - 1);
		}
		return arg.substring(0, node.lastIndexOf('/'));
	}

	// ZooKeeper event thread
	@Override
	public void process(WatchedEvent event) {
		LOGGER.debug("processing WatchedEvent {}", event);
		EventType et = event.getType();
		if (et == EventType.NodeChildrenChanged) {
			LOGGER.debug("processing NodeChildrenChanged {}", event);
			handleThis(event.getPath());
		} else if (et == EventType.NodeDataChanged) {
			// TODO 将事件转化为父节点的事件, 有点太不经济吧？
			LOGGER.debug("processing NodeDataChanged {}", event);
			handleThis(toParentNode(event.getPath()));
		} else {
			LOGGER.debug("got irrelevant event {}", event);
		}
	}
}