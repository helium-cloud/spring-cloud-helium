package org.csource.fastdfs.mgr;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单例实现
 * Created by wuzhiguo on 16-1-7.
 */
public class FastdfsRouteProxy {

	private int maxWait;
	private int maxActive;
	private int maxIdle;
	private int minIdle;
	private int timeout;
	private String charset = "UTF-8";
	private ArrayList<Pool<TrackerClient>> trackerPoolArrays = new ArrayList<>();
	private HashMap<String, Pool<StorageClient>> storagePoolMap = new HashMap<>();
	private static Map<String, String> ipMap = null;
	public FastdfsRouteProxy(int maxWait,
							 int maxActive,
							 int maxIdle,
							 int minIdle,
							 int timeout,
							 String charset,
							 String paramTrackers,
							 String paramStorages,
							 String pIpMap) throws Exception {

		this.maxWait = maxWait;
		this.maxActive = maxActive;
		this.maxIdle = maxIdle;
		this.minIdle = minIdle;
		this.timeout = timeout;
		this.charset = charset;

		List<FastdfsNode> trackerNodes = new ArrayList<>();
		List<FastdfsNode> storageNodes = new ArrayList<>();

		String[] trackers = paramTrackers.split(";");
		for (String tracker : trackers) {
			String[] items = tracker.split(":");
			FastdfsNode node = new FastdfsNode(items[0], Integer.valueOf(items[1]));
			trackerNodes.add(node);
		}

		String[] storages = paramStorages.split(";");
		for (String storage : storages) {
			String[] items = storage.split(":");
			FastdfsNode node = new FastdfsNode(items[0], Integer.valueOf(items[1]));
			storageNodes.add(node);
		}

		if (trackerNodes == null || trackerNodes.size() == 0
				|| storageNodes == null || storageNodes.size() == 0) {
			throw new Exception("FastdfsRouteProxy params error.");
		}

		createFastdfsPool(trackerNodes, storageNodes);

		if ((pIpMap != null) && (pIpMap.trim().length() != 0))
		{
			String[] ipMaps = pIpMap.split(";");
			for (String item : ipMaps)
			{
				String[] pairs = item.split("->");
				if (pairs.length == 2)
				{
					if (ipMap == null)
					{
						ipMap = new HashMap();
					}
					ipMap.put(pairs[0], pairs[1]);
				}
			}
		}
	}

	private void createFastdfsPool(List<FastdfsNode> trackerNodes, List<FastdfsNode> storageNodes) {
		for (int i = 0; i < trackerNodes.size(); i++) {
			FastdfsNode node = trackerNodes.get(i);
			Pool<TrackerClient> pool = getTrackerPool(node);
			trackerPoolArrays.add(pool);
		}
		for (int i = 0; i < storageNodes.size(); i++) {
			FastdfsNode node = storageNodes.get(i);
			Pool<StorageClient> pool = getStoragePool(node);
			storagePoolMap.put(node.toString(), pool);
		}
	}

	private Pool<TrackerClient> getTrackerPool(FastdfsNode node) {
		String ip = node.getIp();
		int port = node.getPort();
		TrackerFactory trackerFactory = new TrackerFactory(ip, port, timeout);
		GenericObjectPool.Config poolConfig = new GenericObjectPool.Config();
		poolConfig.maxActive = maxActive;
		poolConfig.maxIdle = maxIdle;
		poolConfig.maxWait = maxWait;
		poolConfig.minIdle = minIdle;
		poolConfig.testOnBorrow = true;
		Pool<TrackerClient> trackerPool = new Pool<>(poolConfig, trackerFactory);
		return trackerPool;
	}

	private Pool<StorageClient> getStoragePool(FastdfsNode node) {
		String ip = node.getIp();
		int port = node.getPort();
		StorageFactory storageFactory = new StorageFactory(ip, port, timeout);
		GenericObjectPool.Config poolConfig = new GenericObjectPool.Config();
		poolConfig.maxActive = maxActive;
		poolConfig.maxIdle = maxIdle;
		poolConfig.maxWait = maxWait;
		poolConfig.minIdle = minIdle;
		poolConfig.testOnBorrow = true;
		Pool<StorageClient> storagePool = new Pool<>(poolConfig, storageFactory);
		return storagePool;
	}

	public Pool<TrackerClient> getTrackerPool() {
		double random = (Math.random() * trackerPoolArrays.size());
		int keys = Math.abs((int) random);
		int r = keys % trackerPoolArrays.size();
		return trackerPoolArrays.get(r);
	}

	public Pool<StorageClient> getStoragePool(String name) {
		if (ipMap != null)
		{
			String nname = (String)ipMap.get(name);
			if (nname == null)
			{
				return (Pool)storagePoolMap.get(name);
			}
			name = nname;
		}
		return (Pool)storagePoolMap.get(name);
	}

}
