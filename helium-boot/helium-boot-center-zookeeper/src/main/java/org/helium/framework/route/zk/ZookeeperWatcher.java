package org.helium.framework.route.zk;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;

/**
 * 用于监视FAE在Zookeeper的同步数据
 *
 * @author 高磊 gaolei@feinno.com
 */
public class ZookeeperWatcher {
	private static final Logger logger = LoggerFactory.getLogger(ZookeeperWatcher.class);
	private static final int SESSION_TIMEOUT = 10 * 2000; // 好像是zk
	private static final int REFRESH_THRESHOLD = 2 * 1000;

	public static final String NODE_APP = "/FAE/Applications";
	public static final String NODE_WORKER = "/FAE/RunningWorkers";

	private String zkUrl;
	private int zkSessionTimeout = SESSION_TIMEOUT;
	private long refreshThreshold = REFRESH_THRESHOLD;


	private ZooKeeperDataLoader dataLoader;
	private ZooKeeper zk;
	private AtomicBoolean inited = new AtomicBoolean(false);

	/**
	 * @param zkHosts zookeeper连接字符串
	 * @param sessionTimeout zookeeper sessiontimeout (ms)
	 * @param refreshThreshold 路由刷新阈值。 如果路由变动(App或Worker节点)后refreshThresHold ms内再无变动，则触发回调
	 * @throws IOException
	 */
	public ZookeeperWatcher(String zkHosts, int sessionTimeout, long refreshThreshold) throws IOException {
		this.zkUrl = zkHosts;
		this.zkSessionTimeout = sessionTimeout;
		this.refreshThreshold = refreshThreshold;
	}


//	public void setAppsUpdateCallback(Action<List<ApplicationEntity>> callback)
//	{
//		applicationCallback = callback;
//	}
//
//	public void setWorkersUpdaterCallback(Action<List<RunningWorkerEntity>> callback)
//	{
//		runningWorkerCallback = callback;
//	}

	public void init() throws IOException {
		if (inited.compareAndSet(false, true)) {
			internalInit();
		} else {
			logger.warn("already inited.");
		}
	}

//	public List<ApplicationEntity> loadApps() throws KeeperException, InterruptedException
//	{
//		Map<String, byte[]> datas = ZookeeperWatcherHelper.loadPath(zk, NODE_APP, false);
//		return ZookeeperWatcherHelper.parseApplicationData(datas);
//	}
//
//	public List<RunningWorkerEntity> loadWorkers() throws KeeperException, InterruptedException
//	{
//		Map<String, byte[]> datas = ZookeeperWatcherHelper.loadPath(zk, NODE_WORKER, false);
//		return ZookeeperWatcherHelper.parseRunningWorkerData(datas.values());
//	}

	private void internalInit() throws IOException {
		zk = new ZooKeeper(zkUrl, zkSessionTimeout, new SessionWatcher());
		dataLoader = new ZooKeeperDataLoader(refreshThreshold, zk);

//		if (runningWorkerCallback != null) {
//			dataLoader.load(NODE_WORKER, new DataMonitor() {
//				@Override
//				public void update(Map<String, byte[]> datas)
//				{
//					List<RunningWorkerEntity> apps = ZookeeperWatcherHelper.parseRunningWorkerData(datas.values());
//					runningWorkerCallback.run(apps);
//				}
//			});
//		} else {
//			logger.warn("AppsUpdateCallback not set before init.");
//		}
//
//		if (applicationCallback != null) {
//			dataLoader.load(NODE_APP, new DataMonitor() {
//				@Override
//				public void update(Map<String, byte[]> datas)
//				{
//					List<ApplicationEntity> apps = ZookeeperWatcherHelper.parseApplicationData(datas);
//					applicationCallback.run(apps);
//				}
//			});
//		} else {
//			logger.warn("WorkersUpdaterCallback not set before init.");
//		}
	}

	private class SessionWatcher implements org.apache.zookeeper.Watcher {
		private void reConnect() {
			dataLoader.shutdown();

			boolean inited = false;
			while (!inited) { // never give up until re-initialized?
				try {
					internalInit();
					inited = true;
				} catch (IOException e) {
					logger.warn("re-init failed", e);
					try {
						Thread.sleep(2 * 1000); // TODO
					} catch (InterruptedException ie) {
						logger.warn("damn, I am sleeping", ie);
					}
				}
			}
		}

		// single thread (zooKeeper eventThread)
		@Override
		public void process(WatchedEvent event) {
			if (event.getType() == Event.EventType.None) {
				switch (event.getState()) {
					case SyncConnected:
						logger.info("zk Connected");
						break;
					case Disconnected:
						logger.warn("zk disconnected, url {}, passively waiting SyncConnected", zkUrl);
						break;
					case Expired:
					case AuthFailed:
						// It's all over, reconnect
						logger.warn("zk session timeout, re-establishing connection, url {}", zkUrl);
						reConnect();
						break;
					default:
						logger.warn("what? we got an unexpected event. [{}]", event.getState());
				}
			}
		}
	}

}
