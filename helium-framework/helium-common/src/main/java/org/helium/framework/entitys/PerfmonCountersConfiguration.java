package org.helium.framework.entitys;

import org.helium.superpojo.SuperPojo;

import java.util.List;

/**
 * Created by Coral on 11/11/15.
 */
public class PerfmonCountersConfiguration extends SuperPojo {
	public static final int DEFAULT_DELAY_SECONDS = 60;

	private DatabaseNode database;


	private int delaySeconds = 60;

	private String tablePrefix;


	private List<CounterNode> counters;

	public DatabaseNode getDatabase() {
		return database;
	}

	public void setDatabase(DatabaseNode database) {
		this.database = database;
	}

	public String getTablePrefix() {
		return tablePrefix;
	}

	public void setTablePrefix(String tablePrefix) {
		this.tablePrefix = tablePrefix;
	}

	public List<CounterNode> getCounters() {
		return counters;
	}

	public void setCounters(List<CounterNode> counters) {
		this.counters = counters;
	}

	public int getDelaySeconds() {
		return delaySeconds;
	}

	public void setDelaySeconds(int delaySeconds) {
		this.delaySeconds = delaySeconds;
	}

	public static class CounterNode extends SuperPojo {

		private String counterName;

		private int interval;

		public String getCounterName() {
			return counterName;
		}

		public void setCounterName(String counterName) {
			this.counterName = counterName;
		}

		public int getInterval() {
			return interval;
		}

		public void setInterval(int interval) {
			this.interval = interval;
		}
	}
}
