/**
 * Copyright (C) 2008 Happy Fish / YuQing
 * <p>
 * FastDFS Java Client may be copied only under the terms of the GNU Lesser
 * General Public License (LGPL).
 * Please visit the FastDFS Home Page http://www.csource.org/ for more detail.
 */

package org.csource.fastdfs;


/**
 * Storage Server Info
 * @author Happy Fish / YuQing
 * @version Version 1.11
 */
public class StorageServer extends TrackerServer {
	protected int store_path_index = 0;

	/**
	 * Constructor
	 * @param ip_addr the ip address of storage server
	 * @param port the port of storage server
	 * @param store_path the store path index on the storage server
	 */
	public StorageServer(String ip_addr, int port, int timeout) {
		super(ip_addr, port, timeout);
	}


	public void setStore_path_index(int store_path_index) {
		this.store_path_index = store_path_index;
	}

	public void setStore_path_index(byte store_path_index) {
		if (store_path_index < 0) {
			this.store_path_index = 256 + store_path_index;
		} else {
			this.store_path_index = store_path_index;
		}
	}


	/**
	 * @return the store path index on the storage server
	 */
	public int getStorePathIndex() {
		return this.store_path_index;
	}
}
