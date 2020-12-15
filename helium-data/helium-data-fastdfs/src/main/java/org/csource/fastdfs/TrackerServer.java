/**
 * Copyright (C) 2008 Happy Fish / YuQing
 * <p>
 * FastDFS Java Client may be copied only under the terms of the GNU Lesser
 * General Public License (LGPL).
 * Please visit the FastDFS Home Page http://www.csource.org/ for more detail.
 */

package org.csource.fastdfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


/**
 * Tracker Server Info
 * @author Happy Fish / YuQing
 * @version Version 1.11
 */
public class TrackerServer {
	protected Socket sock;
	protected InetSocketAddress inetSockAddr;
	protected int timeout;
	protected OutputStream out;
	protected InputStream in;

	/**
	 * Constructor
	 * @param sock Socket of server
	 * @param inetSockAddr the server info
	 */
	public TrackerServer(String host, int port, int timeout) {
		inetSockAddr = new InetSocketAddress(host, port);
		this.timeout = timeout;
	}

	/**
	 * get the connected socket
	 * @return the socket
	 */
	public Socket getSocket() throws IOException {
		if (!isConnected()) {
			sock = new Socket();
			sock.setSoTimeout(timeout);
			sock.connect(inetSockAddr, timeout);
			out = this.sock.getOutputStream();
			in = this.sock.getInputStream();
		}

		return this.sock;
	}

	public boolean isConnected() {
		return sock != null && sock.isBound() && !sock.isClosed() && sock.isConnected() && !sock.isInputShutdown() && !sock.isOutputShutdown();
	}

	/**
	 * get the server info
	 * @return the server info
	 */
	public InetSocketAddress getInetSocketAddress() {
		return this.inetSockAddr;
	}

	public OutputStream getOutputStream() throws IOException {
		return out;
	}

	public InputStream getInputStream() throws IOException {
		return in;
	}

	public void close() throws IOException {
		if (isConnected()) {
			try {
				ProtoCommon.closeSocket(this.out);
			} finally {
				if (isConnected()) {
					out.close();
					in.close();
					sock.close();
					this.sock = null;
				}

			}
		}
	}

	public boolean ping() {
		try {
			return ProtoCommon.activeTest(out, in);
		} catch (Exception e) {
			return false;
		}
	}


	protected void finalize() throws Throwable {
		this.close();
	}
}
