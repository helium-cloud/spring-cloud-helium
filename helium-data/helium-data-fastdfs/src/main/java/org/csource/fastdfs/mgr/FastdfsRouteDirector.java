package org.csource.fastdfs.mgr;

import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FastdfsRouteDirector {

	private static int maxWait;
	private static int maxActive;
	private static int maxIdle;
	private static int minIdle;
	private static int timeout;
	private static String charset = "UTF-8";
	private static ArrayList<Pool<TrackerClient>> trackerPoolArrays = new ArrayList<>();
	private static HashMap<String, Pool<StorageClient>> storagePoolMap = new HashMap<>();
	private static Map<String, String> ipMap = null;
	public static String getCharset() {
		return charset;
	}

	public static synchronized void initialize(int paramMaxWait,
											   int paramMaxActive,
											   int paramMaxIdle,
											   int paramMinIdle,
											   int paramTimeout,
											   String paramCharset,
											   String paramTrackers,
											   String paramStorages,
											   String pIpMap) throws Exception {
		maxWait = paramMaxWait;
		maxActive = paramMaxActive;
		maxIdle = paramMaxIdle;
		minIdle = paramMinIdle;
		timeout = paramTimeout;
		charset = paramCharset;

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
			throw new Exception("FastdfsRouteDirector initialize params error.");
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

	public static Pool<StorageClient> getStoragePool(String name)
	{
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

	public static Pool<TrackerClient> getTrackerPool() {
		double random = (Math.random() * trackerPoolArrays.size());
		int keys = Math.abs((int) random);
		int r = keys % trackerPoolArrays.size();
		return trackerPoolArrays.get(r);
	}

	private static void createFastdfsPool(List<FastdfsNode> trackerNodes, List<FastdfsNode> storageNodes) {
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

	private static Pool<TrackerClient> getTrackerPool(FastdfsNode node) {
		String ip = node.getIp();
		int port = node.getPort();
		TrackerFactory trackerFactory = new TrackerFactory(ip, port, timeout);
		Config poolConfig = new Config();
		poolConfig.maxActive = maxActive;
		poolConfig.maxIdle = maxIdle;
		poolConfig.maxWait = maxWait;
		poolConfig.minIdle = minIdle;
		poolConfig.testOnBorrow = true;
		Pool<TrackerClient> trackerPool = new Pool<TrackerClient>(poolConfig, trackerFactory);
		return trackerPool;
	}

	private static Pool<StorageClient> getStoragePool(FastdfsNode node) {
		String ip = node.getIp();
		int port = node.getPort();
		StorageFactory storageFactory = new StorageFactory(ip, port, timeout);
		Config poolConfig = new Config();
		poolConfig.maxActive = maxActive;
		poolConfig.maxIdle = maxIdle;
		poolConfig.maxWait = maxWait;
		poolConfig.minIdle = minIdle;
		poolConfig.testOnBorrow = true;
		Pool<StorageClient> storagePool = new Pool<StorageClient>(poolConfig, storageFactory);
		return storagePool;
	}


}
