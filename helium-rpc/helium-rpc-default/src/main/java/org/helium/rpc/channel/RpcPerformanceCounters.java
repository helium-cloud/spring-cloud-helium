package org.helium.rpc.channel;

import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.perfmon.PerformanceCounterType;
import org.helium.perfmon.SmartCounter;
import org.helium.perfmon.annotation.PerformanceCounter;
import org.helium.perfmon.annotation.PerformanceCounterCategory;

public class RpcPerformanceCounters {
	public static void initialize() {
		PerformanceCounterFactory.getCounters(ClientCounter.class, "");
		PerformanceCounterFactory.getCounters(ServerCounter.class, "");
	}

	@PerformanceCounterCategory("rpc-server")
	public static class ServerCounter {
		@PerformanceCounter(name = "tx", type = PerformanceCounterType.TRANSACTION)
		private SmartCounter tx;

		public SmartCounter getTx() {
			return tx;
		}

		public void setTx(SmartCounter tx) {
			this.tx = tx;
		}
	}

	@PerformanceCounterCategory("rpc-client")
	public static class ClientCounter {
		@PerformanceCounter(name = "tx", type = PerformanceCounterType.TRANSACTION)
		private SmartCounter tx;

		public SmartCounter getTx() {
			return tx;
		}

		public void setTx(SmartCounter tx) {
			this.tx = tx;
		}
	}

	public static SmartCounter getClientTxCounter(String name) {
		ClientCounter c = PerformanceCounterFactory.getCounters(ClientCounter.class, name);
		return c.getTx();
	}

	public static SmartCounter getServerTxCounter(String name) {
		ServerCounter c = PerformanceCounterFactory.getCounters(ServerCounter.class, name);
		return c.getTx();
	}
}
