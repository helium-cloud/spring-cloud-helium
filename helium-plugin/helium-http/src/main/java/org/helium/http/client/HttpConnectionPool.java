package org.helium.http.client;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

class HttpConnectionPool {

	ConcurrentHashMap<String, ConcurrentLinkedQueue<TimeLimitedConnection>> ServiceTree;

	HttpConnectionPool() {
		ServiceTree = new ConcurrentHashMap<String, ConcurrentLinkedQueue<TimeLimitedConnection>>();
	}

	public void add(Channel conn) {
		if (conn.isConnected() == false) {
			conn.close();
			return;
		}
		InetSocketAddress addr = (InetSocketAddress) conn.getRemoteAddress();
		String key = String.format("%s:%s", addr.getHostString(), addr.getPort());
		synchronized (ServiceTree) {
			if (ServiceTree.containsKey(key) == false) {
				ConcurrentLinkedQueue<TimeLimitedConnection> tmp = new ConcurrentLinkedQueue<TimeLimitedConnection>();
				ServiceTree.put(key, tmp);
			}
			ServiceTree.get(key).add(new TimeLimitedConnection(conn));
		}
	}

	public Channel poll(String key) {
		Channel conn = null;
		while (true) {
			synchronized (ServiceTree) {
				if (ServiceTree.containsKey(key) == false)
					return null;
				TimeLimitedConnection tlConnection = ServiceTree.get(key).poll();
				if (tlConnection == null)
					return null;
				conn = tlConnection.getConnection();
				if (conn.isConnected() == false) {
					conn.close();
				}else {
					break;
				}
			}
		}
		return  conn;
	}

	public void dispose() {
		for (String key : ServiceTree.keySet()) {
			for (TimeLimitedConnection tc : ServiceTree.get(key)) {
				Channel conn = tc.getConnection();
				if (conn != null)
					conn.close();
			}
			ServiceTree.remove(key);
		}
	}

	class TimeLimitedConnection {
		protected Channel connection;
		protected Timeout timeout = null;

		TimeLimitedConnection(Channel conn) {
			this.connection = conn;
			timeout = HttpClient.timer.newTimeout(new TimerTask() {
				public void run(Timeout timeout) throws Exception {
					synchronized (ServiceTree) {
						InetSocketAddress addr = (InetSocketAddress) connection.getRemoteAddress();
						String key = String.format("%s:%s", addr.getHostString(), addr.getPort());
						if (ServiceTree.containsKey(key) == true)
							if (ServiceTree.get(key).remove(TimeLimitedConnection.this) == true) {
								if (connection == null)
									return;
								connection.close();
							}
					}
				}
			}, 40 * 1000, TimeUnit.MILLISECONDS);
		}

		Channel getConnection() {
			timeout.cancel();
			return connection;
		}
	}
}