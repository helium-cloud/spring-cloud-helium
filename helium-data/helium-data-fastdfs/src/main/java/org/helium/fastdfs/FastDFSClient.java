package org.helium.fastdfs;

import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.csource.fastdfs.mgr.FastdfsNode;
import org.csource.fastdfs.mgr.FastdfsRouteProxy;
import org.csource.fastdfs.mgr.Pool;
import org.helium.fastdfs.spi.FastDFSCounters;
import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.perfmon.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by lvmingwei on 16-1-4.
 */
public class FastDFSClient implements FastDFS {

	private static final Logger LOGGER = LoggerFactory.getLogger(FastDFSClient.class);

	private static final int UPLOAD = 1;
	private static final int FETCH = 2;
	private static final int UPDATE = 3;

	private FastdfsRouteProxy routeProxy;
	private FastDFSCounters counters;

	public FastDFSClient(Properties props) throws Exception {

		int maxWait = Integer.parseInt(props.getProperty("maxWait", "100"));
		int maxActive = Integer.parseInt(props.getProperty("maxActive", "8"));
		int maxIdle = Integer.parseInt(props.getProperty("maxIdle", "8"));
		int minIdle = Integer.parseInt(props.getProperty("minIdle", "0"));
		int timeout = Integer.parseInt(props.getProperty("timeout", "5000"));
		String charset = props.getProperty("charset", "UTF-8");
		String trackers = props.getProperty("trackers", "");
		String storages = props.getProperty("storages", "");
		String ipmap = props.getProperty("ip_map", "");
		routeProxy = new FastdfsRouteProxy(maxWait, maxActive, maxIdle, minIdle, timeout, charset, trackers, storages, ipmap);
		counters = PerformanceCounterFactory.getCounters(FastDFSCounters.class, "FastDFS-Client");
		LOGGER.info("FastDFSClient create success. trackers: {} storages: {} ", trackers, storages);
	}

	@Override
	public String uploadFile(byte[] fileBuffer, String fileExtName, Map<String, String> metaMap, byte storyPath) throws Exception {
		Pool<StorageClient> storagePool = null;
		StorageClient storageClient = null;
		String fileId = null;
		Stopwatch watch = counters.getTxUploadFile().begin();
		try {
			ServerInfo serverInfo = getStorageServerInfo(UPLOAD);
			if (serverInfo == null) {
				watch.fail("getStorageServerInfo Failed.");
				return null;
			}

			if (storyPath >= 0) {
				serverInfo.setStoryPath(storyPath);
			}

			FastdfsNode node = new FastdfsNode(serverInfo.getIpAddr(), serverInfo.getPort());
			storagePool = routeProxy.getStoragePool(node.toString());
			storageClient = storagePool.getResource();
			storageClient.setStoragePathIndex(serverInfo.getStoryPath());
			NameValuePair[] metaList = convertMap2NameValuePair(metaMap);
			String[] parts = storageClient.upload_file(fileBuffer, fileExtName, metaList);
			if (parts != null) {
				fileId = getFileId(parts);
			}

			storagePool.returnResource(storageClient);
			watch.end();
			LOGGER.info("uploadFile() Completed. fileId: {} serverInfo: {} ", fileId, serverInfo);

		} catch (Exception e) {
			LOGGER.error("uploadFile() Error.", e);
			if (storageClient != null) {
				storagePool.returnBrokenResource(storageClient);
			}
			watch.fail(e);
		}

		return fileId;
	}

	@Override
	public String[] uploadFile(byte[] fileBuffer, String fileExtName, Map<String, String> metaMap) throws Exception {
		Pool<StorageClient> storagePool = null;
		StorageClient storageClient = null;
		String[] parts = null;
		Stopwatch watch = counters.getTxUploadFile().begin();
		try {
			ServerInfo serverInfo = getStorageServerInfo(UPLOAD);
			if (serverInfo == null) {
				watch.fail("getStorageServerInfo Failed.");
				return null;
			}

			FastdfsNode node = new FastdfsNode(serverInfo.getIpAddr(), serverInfo.getPort());
			storagePool = routeProxy.getStoragePool(node.toString());
			storageClient = storagePool.getResource();
			storageClient.setStoragePathIndex(serverInfo.getStoryPath());
			NameValuePair[] metaList = convertMap2NameValuePair(metaMap);
			parts = storageClient.upload_file(fileBuffer, fileExtName, metaList);

			storagePool.returnResource(storageClient);
			watch.end();
			LOGGER.info("uploadFile() Completed. fileId: {} serverInfo: {} ", parts, serverInfo);

		} catch (Exception e) {
			LOGGER.error("uploadFile() Error.", e);
			if (storageClient != null) {
				storagePool.returnBrokenResource(storageClient);
			}
			watch.fail(e);
		}
		return parts;
	}

	@Override
	public String upload_file1(String master_file_id, String prefix_name, byte[] file_buff, String file_ext_name, Map<String, String> metaMap)
			throws IOException, MyException {
		Pool<StorageClient> storagePool = null;
		StorageClient storageClient = null;
		String parts = null;
		Stopwatch watch = counters.getTxUploadFile().begin();
		try {
			ServerInfo serverInfo = getStorageServerInfo(UPLOAD);
			if (serverInfo == null) {
				watch.fail("getStorageServerInfo Failed.");
				return null;
			}

			FastdfsNode node = new FastdfsNode(serverInfo.getIpAddr(), serverInfo.getPort());
			storagePool = routeProxy.getStoragePool(node.toString());
			storageClient = storagePool.getResource();
			storageClient.setStoragePathIndex(serverInfo.getStoryPath());
			NameValuePair[] metaList = convertMap2NameValuePair(metaMap);
			parts = storageClient.upload_file1(master_file_id, prefix_name, file_buff, file_ext_name, metaList);

			storagePool.returnResource(storageClient);
			watch.end();
			LOGGER.info("upload_file1() Completed. fileId: {} serverInfo: {} ", parts, serverInfo);

		} catch (Exception e) {
			LOGGER.error("upload_file1() Error.", e);
			if (storageClient != null) {
				storagePool.returnBrokenResource(storageClient);
			}
			watch.fail(e);
		}
		return parts;
	}

	@Override
	public String uploadAppenderFile(byte[] fileBuffer, String fileExtName, Map<String, String> metaMap, byte storyPath) throws Exception {
		Pool<StorageClient> storagePool = null;
		StorageClient storageClient = null;
		String appenderFileId = null;
		Stopwatch watch = counters.getTxUploadAppenderFile().begin();
		try {
			ServerInfo serverInfo = getStorageServerInfo(UPLOAD);
			if (serverInfo == null) {
				watch.fail("getStorageServerInfo Failed.");
				return null;
			}

			if (storyPath >= 0) {
				serverInfo.setStoryPath(storyPath);
			}

			FastdfsNode node = new FastdfsNode(serverInfo.getIpAddr(), serverInfo.getPort());
			storagePool = routeProxy.getStoragePool(node.toString());
			storageClient = storagePool.getResource();
			storageClient.setStoragePathIndex(serverInfo.getStoryPath());
			NameValuePair[] metaList = convertMap2NameValuePair(metaMap);
			String[] parts = storageClient.upload_appender_file(fileBuffer, fileExtName, metaList);
			if (parts != null) {
				appenderFileId = getFileId(parts);
			}

			storagePool.returnResource(storageClient);
			watch.end();
			LOGGER.info("uploadAppenderFile() Completed. fileId: {} serverInfo: {} ", appenderFileId, serverInfo);

		} catch (Exception e) {
			LOGGER.error("uploadAppenderFile() Error.", e);
			if (storageClient != null) {
				storagePool.returnBrokenResource(storageClient);
			}
			watch.fail(e);
		}

		return appenderFileId;
	}

	@Override
	public int appendFile(String appenderFileId, byte[] fileBuffer) throws Exception {
		Pool<StorageClient> storagePool = null;
		StorageClient storageClient = null;
		int result = -1;
		Stopwatch watch = counters.getTxAppendFile().begin();
		try {
			String[] parts = splitFileId(appenderFileId);
			if (parts == null) {
				watch.fail("splitFileId Failed.");
				return -2;
			}

			String groupName = parts[0];
			String appenderFilename = parts[1];
			ServerInfo serverInfo = getStorageServerInfo(UPDATE, groupName, appenderFilename);
			if (serverInfo == null) {
				watch.fail("getStorageServerInfo Failed.");
				return -3;
			}

			FastdfsNode node = new FastdfsNode(serverInfo.getIpAddr(), serverInfo.getPort());
			storagePool = routeProxy.getStoragePool(node.toString());
			storageClient = storagePool.getResource();
			result = storageClient.append_file(groupName, appenderFilename, fileBuffer);
			storagePool.returnResource(storageClient);
			watch.end();
			LOGGER.info("appendFile() Completed. fileId: {} serverInfo: {} ", appenderFileId, serverInfo);

		} catch (Exception e) {
			LOGGER.error(String.format("appendFile() Error. fileId: %s", appenderFileId), e);
			if (storageClient != null) {
				storagePool.returnBrokenResource(storageClient);
			}
			watch.fail(e);
		}

		return result;
	}

	@Override
	public int appendFile(String appenderFileId, byte[] fileBuffer, int offset, int length) throws Exception {
		Pool<StorageClient> storagePool = null;
		StorageClient storageClient = null;
		int result = -1;
		Stopwatch watch = counters.getTxAppendFileOffset().begin();
		try {
			String[] parts = splitFileId(appenderFileId);
			if (parts == null) {
				watch.fail("splitFileId Failed.");
				return -2;
			}

			String groupName = parts[0];
			String appenderFilename = parts[1];
			ServerInfo serverInfo = getStorageServerInfo(UPDATE, groupName, appenderFilename);
			if (serverInfo == null) {
				watch.fail("getStorageServerInfo Failed.");
				return -3;
			}

			FastdfsNode node = new FastdfsNode(serverInfo.getIpAddr(), serverInfo.getPort());
			storagePool = routeProxy.getStoragePool(node.toString());
			storageClient = storagePool.getResource();
			result = storageClient.append_file(groupName, appenderFilename, fileBuffer, offset, length);
			storagePool.returnResource(storageClient);
			watch.end();
			LOGGER.info("appendFile() Completed. fileId: {} serverInfo: {} ", appenderFileId, serverInfo);

		} catch (Exception e) {
			LOGGER.error(String.format("appendFile() Error. fileId: %s", appenderFileId), e);
			if (storageClient != null) {
				storagePool.returnBrokenResource(storageClient);
			}
			watch.fail(e);
		}

		return result;
	}

	@Override
	public int modifyFile(String appenderFileId, long fileOffset, byte[] fileBuffer) throws Exception {
		Pool<StorageClient> storagePool = null;
		StorageClient storageClient = null;
		int result = -1;
		Stopwatch watch = counters.getTxModifyFile().begin();
		try {
			String[] parts = splitFileId(appenderFileId);
			if (parts == null) {
				watch.fail("splitFileId Failed.");
				return -2;
			}

			String groupName = parts[0];
			String appenderFilename = parts[1];
			ServerInfo serverInfo = getStorageServerInfo(UPDATE, groupName, appenderFilename);
			if (serverInfo == null) {
				watch.fail("getStorageServerInfo Failed.");
				return -3;
			}

			FastdfsNode node = new FastdfsNode(serverInfo.getIpAddr(), serverInfo.getPort());
			storagePool = routeProxy.getStoragePool(node.toString());
			storageClient = storagePool.getResource();
			result = storageClient.modify_file(groupName, appenderFilename, fileOffset, fileBuffer);
			storagePool.returnResource(storageClient);
			watch.end();
			LOGGER.info("modifyFile() Completed. fileId: {} serverInfo: {} ", appenderFileId, serverInfo);

		} catch (Exception e) {
			LOGGER.error(String.format("modifyFile() Error. fileId: %s", appenderFileId), e);
			if (storageClient != null) {
				storagePool.returnBrokenResource(storageClient);
			}
			watch.fail(e);
		}

		return result;
	}

	@Override
	public int modifyFile(String appenderFileId, long fileOffset, byte[] fileBuffer, int bufferOffset, int bufferLength) throws Exception {
		Pool<StorageClient> storagePool = null;
		StorageClient storageClient = null;
		int result = -1;
		Stopwatch watch = counters.getTxModifyFileOffset().begin();
		try {
			String[] parts = splitFileId(appenderFileId);
			if (parts == null) {
				watch.fail("splitFileId Failed.");
				return -2;
			}

			String groupName = parts[0];
			String appenderFilename = parts[1];
			ServerInfo serverInfo = getStorageServerInfo(UPDATE, groupName, appenderFilename);
			if (serverInfo == null) {
				watch.fail("getStorageServerInfo Failed.");
				return -3;
			}

			FastdfsNode node = new FastdfsNode(serverInfo.getIpAddr(), serverInfo.getPort());
			storagePool = routeProxy.getStoragePool(node.toString());
			storageClient = storagePool.getResource();
			result = storageClient.modify_file(groupName, appenderFilename, fileOffset, fileBuffer, bufferOffset, bufferLength);
			storagePool.returnResource(storageClient);
			watch.end();
			LOGGER.info("modifyFile() Completed. fileId: {} serverInfo: {} ", appenderFileId, serverInfo);

		} catch (Exception e) {
			LOGGER.error(String.format("modifyFile() Error. fileId: %s", appenderFileId), e);
			if (storageClient != null) {
				storagePool.returnBrokenResource(storageClient);
			}
			watch.fail(e);
		}

		return result;
	}

	@Override
	public int truncateFile(String appenderFileId, long truncatedFileSize) throws Exception {
		Pool<StorageClient> storagePool = null;
		StorageClient storageClient = null;
		int result = -1;
		Stopwatch watch = counters.getTxTruncateFile().begin();
		try {
			String[] parts = splitFileId(appenderFileId);
			if (parts == null) {
				watch.fail("splitFileId Failed.");
				return -2;
			}

			String groupName = parts[0];
			String appenderFilename = parts[1];
			ServerInfo serverInfo = getStorageServerInfo(UPDATE, groupName, appenderFilename);
			if (serverInfo == null) {
				watch.fail("getStorageServerInfo Failed.");
				return -3;
			}

			FastdfsNode node = new FastdfsNode(serverInfo.getIpAddr(), serverInfo.getPort());
			storagePool = routeProxy.getStoragePool(node.toString());
			storageClient = storagePool.getResource();
			result = storageClient.truncate_file(groupName, appenderFilename, truncatedFileSize);
			storagePool.returnResource(storageClient);
			watch.end();
			LOGGER.info("truncateFile() Completed. fileId: {} serverInfo: {} ", appenderFileId, serverInfo);

		} catch (Exception e) {
			LOGGER.error(String.format("truncateFile() Error. fileId: %s", appenderFileId), e);
			if (storageClient != null) {
				storagePool.returnBrokenResource(storageClient);
			}
			watch.fail(e);
		}

		return result;
	}

	@Override
	public int deleteFile(String fileId) throws Exception {
		Pool<StorageClient> storagePool = null;
		StorageClient storageClient = null;
		int result = -1;
		Stopwatch watch = counters.getTxDeleteFile().begin();
		try {
			String[] parts = splitFileId(fileId);
			if (parts == null) {
				watch.fail("splitFileId Failed.");
				return -2;
			}

			String groupName = parts[0];
			String filename = parts[1];
			ServerInfo serverInfo = getStorageServerInfo(UPDATE, groupName, filename);
			if (serverInfo == null) {
				watch.fail("getStorageServerInfo Failed.");
				return -3;
			}

			FastdfsNode node = new FastdfsNode(serverInfo.getIpAddr(), serverInfo.getPort());
			storagePool = routeProxy.getStoragePool(node.toString());
			storageClient = storagePool.getResource();
			result = storageClient.delete_file(groupName, filename);
			storagePool.returnResource(storageClient);
			watch.end();
			LOGGER.info("deleteFile() Completed. fileId: {} serverInfo: {} ", fileId, serverInfo);

		} catch (Exception e) {
			LOGGER.error(String.format("deleteFile() Error. fileId: %s", fileId), e);
			if (storageClient != null) {
				storagePool.returnBrokenResource(storageClient);
			}
			watch.fail(e);
		}

		return result;
	}

	@Override
	public byte[] downloadFile(String fileId) throws Exception {
		Pool<StorageClient> storagePool = null;
		StorageClient storageClient = null;
		byte[] fileBuffer = null;
		Stopwatch watch = counters.getTxDownloadFile().begin();
		try {
			String[] parts = splitFileId(fileId);
			if (parts == null) {
				watch.fail("splitFileId Failed.");
				return null;
			}

			String groupName = parts[0];
			String filename = parts[1];
			ServerInfo serverInfo = getStorageServerInfo(FETCH, groupName, filename);
			if (serverInfo == null) {
				watch.fail("getStorageServerInfo Failed.");
				return null;
			}

			FastdfsNode node = new FastdfsNode(serverInfo.getIpAddr(), serverInfo.getPort());
			storagePool = routeProxy.getStoragePool(node.toString());
			storageClient = storagePool.getResource();
			fileBuffer = storageClient.download_file(groupName, filename);
			storagePool.returnResource(storageClient);
			watch.end();
			LOGGER.info("downloadFile() Completed. fileId: {} serverInfo: {} ", fileId, serverInfo);

		} catch (Exception e) {
			LOGGER.error(String.format("downloadFile() Error. fileId: %s", fileId), e);
			if (storageClient != null) {
				storagePool.returnBrokenResource(storageClient);
			}
			watch.fail(e);
		}

		return fileBuffer;
	}

	@Override
	public byte[] downloadFile(String fileId, long fileOffset, long downloadBytes) throws Exception {
		Pool<StorageClient> storagePool = null;
		StorageClient storageClient = null;
		byte[] fileBuffer = null;
		Stopwatch watch = counters.getTxDownloadFileOffset().begin();
		try {
			String[] parts = splitFileId(fileId);
			if (parts == null) {
				watch.fail("splitFileId Failed.");
				return null;
			}

			String groupName = parts[0];
			String filename = parts[1];
			ServerInfo serverInfo = getStorageServerInfo(FETCH, groupName, filename);
			if (serverInfo == null) {
				watch.fail("getStorageServerInfo Failed.");
				return null;
			}

			FastdfsNode node = new FastdfsNode(serverInfo.getIpAddr(), serverInfo.getPort());
			storagePool = routeProxy.getStoragePool(node.toString());
			storageClient = storagePool.getResource();
			fileBuffer = storageClient.download_file(groupName, filename, fileOffset, downloadBytes);
			storagePool.returnResource(storageClient);
			watch.end();
			LOGGER.info("downloadFile() Completed. fileId: {} serverInfo: {} ", fileId, serverInfo);

		} catch (Exception e) {
			LOGGER.error(String.format("downloadFile() Error. fileId: %s", fileId), e);
			if (storageClient != null) {
				storagePool.returnBrokenResource(storageClient);
			}
			watch.fail(e);
		}

		return fileBuffer;
	}

	@Override
	public FileInfo queryFileInfo(String fileId) throws Exception {
		Pool<StorageClient> storagePool = null;
		StorageClient storageClient = null;
		FileInfo fileInfo = null;
		Stopwatch watch = counters.getTxQueryFileInfo().begin();
		try {
			String[] parts = splitFileId(fileId);
			if (parts == null) {
				watch.fail("splitFileId Failed.");
				return null;
			}

			String groupName = parts[0];
			String filename = parts[1];
			ServerInfo serverInfo = getStorageServerInfo(UPDATE, groupName, filename);
			if (serverInfo == null) {
				watch.fail("getStorageServerInfo Failed.");
				return null;
			}

			FastdfsNode node = new FastdfsNode(serverInfo.getIpAddr(), serverInfo.getPort());
			storagePool = routeProxy.getStoragePool(node.toString());
			storageClient = storagePool.getResource();
			fileInfo = storageClient.query_file_info(groupName, filename);
			storagePool.returnResource(storageClient);
			watch.end();
			LOGGER.info("queryFileInfo() Completed. fileId: {} serverInfo: {} ", fileId, serverInfo);

		} catch (Exception e) {
			LOGGER.error(String.format("queryFileInfo() Error. fileId: %s", fileId), e);
			if (storageClient != null) {
				storagePool.returnBrokenResource(storageClient);
			}
			watch.fail(e);
		}

		return fileInfo;
	}

	@Override
	public Map<String, String> getMetadata(String fileId) throws Exception {
		Pool<StorageClient> storagePool = null;
		StorageClient storageClient = null;
		Map<String, String> metaMap = null;
		Stopwatch watch = counters.getTxGetMetadata().begin();
		try {
			String[] parts = splitFileId(fileId);
			if (parts == null) {
				watch.fail("splitFileId Failed.");
				return null;
			}

			String groupName = parts[0];
			String filename = parts[1];
			ServerInfo serverInfo = getStorageServerInfo(UPDATE, groupName, filename);
			if (serverInfo == null) {
				watch.fail("getStorageServerInfo Failed.");
				return null;
			}

			FastdfsNode node = new FastdfsNode(serverInfo.getIpAddr(), serverInfo.getPort());
			storagePool = routeProxy.getStoragePool(node.toString());
			storageClient = storagePool.getResource();
			NameValuePair[] pairs = storageClient.get_metadata(groupName, filename);
			metaMap = convertNameValuePair2Map(pairs);
			storagePool.returnResource(storageClient);
			watch.end();
			LOGGER.info("getMetadata() Completed. fileId: {} serverInfo: {} ", fileId, serverInfo);

		} catch (Exception e) {
			LOGGER.error(String.format("getMetadata() Error. fileId: %s", fileId), e);
			if (storageClient != null) {
				storagePool.returnBrokenResource(storageClient);
			}
			watch.fail(e);
		}

		return metaMap;
	}

	@Override
	public int setMetadata(String fileId, Map<String, String> metaMap) throws Exception {
		Pool<StorageClient> storagePool = null;
		StorageClient storageClient = null;
		int result = -1;
		Stopwatch watch = counters.getTxSetMetadata().begin();
		try {
			String[] parts = splitFileId(fileId);
			if (parts == null) {
				watch.fail("splitFileId Failed.");
				return -2;
			}

			String groupName = parts[0];
			String filename = parts[1];
			ServerInfo serverInfo = getStorageServerInfo(UPDATE, groupName, filename);
			if (serverInfo == null) {
				watch.fail("getStorageServerInfo Failed.");
				return -3;
			}

			FastdfsNode node = new FastdfsNode(serverInfo.getIpAddr(), serverInfo.getPort());
			storagePool = routeProxy.getStoragePool(node.toString());
			storageClient = storagePool.getResource();
			NameValuePair[] metaList = convertMap2NameValuePair(metaMap);
			result = storageClient.set_metadata(groupName, filename, metaList, ProtoCommon.STORAGE_SET_METADATA_FLAG_MERGE);
			storagePool.returnResource(storageClient);
			watch.end();
			LOGGER.info("setMetadata() Completed. fileId: {} serverInfo: {} ", fileId, serverInfo);

		} catch (Exception e) {
			LOGGER.error(String.format("setMetadata() Error. fileId: %s", fileId), e);
			if (storageClient != null) {
				storagePool.returnBrokenResource(storageClient);
			}
			watch.fail(e);
		}

		return result;
	}

	@Override
	public FastdfsRouteProxy getProxy() {
		return routeProxy;
	}

	private ServerInfo getStorageServerInfo(int tag) {
		return getStorageServerInfo(tag, null, null);
	}

	private ServerInfo getStorageServerInfo(int tag, String groupName, String filename) {
		Pool<TrackerClient> trackerPool = null;
		TrackerClient trackerClient = null;
		ServerInfo info = null;
		try {
			trackerPool = routeProxy.getTrackerPool();
			trackerClient = trackerPool.getResource();
			switch (tag) {
				case UPLOAD:
					info = trackerClient.getStoreStorage();
					break;
				case UPDATE:
					info = trackerClient.getUpdateStorage(groupName, filename);
					break;
				case FETCH:
					info = trackerClient.getFetchStorage(groupName, filename);
					break;
			}

			trackerPool.returnResource(trackerClient);
			LOGGER.info("getStorageServerInfo() Completed. tag: {} groupName: {} filename: {} serverInfo: {}", tag, groupName, filename, info);

		} catch (Exception e) {
			LOGGER.error(String.format("getStorageServerInfo() Error. tag: %s groupName: %s filename: %s", tag, groupName, filename), e);
			if (trackerClient != null) {
				trackerPool.returnBrokenResource(trackerClient);
			}
		}

		return info;
	}

	private NameValuePair[] convertMap2NameValuePair(Map<String, String> map) {
		if (map != null) {
			int index = 0;
			NameValuePair[] pairs = new NameValuePair[map.size()];
			for (Map.Entry<String, String> entry : map.entrySet()) {
				pairs[index++] = new NameValuePair(entry.getKey(), entry.getValue());
			}

			return pairs;
		}

		return null;
	}

	private Map<String, String> convertNameValuePair2Map(NameValuePair[] pairs) {
		if (pairs != null) {
			Map<String, String> map = new HashMap<>();
			for (int i = 0; i < pairs.length; i++) {
				map.put(pairs[i].getName(), pairs[i].getValue());
			}

			return map;
		}

		return null;
	}

	private String getFileId(String[] parts) {
		return String.format("%s/%s", parts[0], parts[1]);
	}

	private String[] splitFileId(String fileId) {
		int index = fileId.indexOf("/");
		if (index > 0 && index < fileId.length() - 1) {
			String[] parts = new String[2];
			parts[0] = fileId.substring(0, index);  // group name
			parts[1] = fileId.substring(index + 1); // file name
			return parts;
		}

		return null;
	}

}
