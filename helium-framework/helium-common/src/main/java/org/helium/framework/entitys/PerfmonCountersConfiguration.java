package org.helium.framework.entitys;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Childs;
import com.feinno.superpojo.annotation.Entity;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

import java.util.List;

/**
 * Created by Coral on 11/11/15.
 */
@Entity(name = "perfmon")
public class PerfmonCountersConfiguration extends SuperPojo {
	public static final int DEFAULT_DELAY_SECONDS = 60;

	@Field(id = 1, name = "database", type = NodeType.NODE)
	private DatabaseNode database;

	@Field(id = 2, name = "delaySeconds", type = NodeType.NODE)
	private int delaySeconds = 60;

	@Field(id = 3, name = "tablePrefix", type = NodeType.NODE)
	private String tablePrefix;

	@Childs(id = 11, parent = "counters", child = "counter")
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
		@Field(id = 1, name = "name", type = NodeType.ATTR)
		private String counterName;

		@Field(id = 2, name = "interval", type = NodeType.ATTR)
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
