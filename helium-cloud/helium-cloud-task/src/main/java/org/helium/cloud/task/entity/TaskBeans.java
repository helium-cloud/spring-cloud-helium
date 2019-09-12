package org.helium.cloud.task.entity;

/**
 * Created by Coral on 7/5/15.
 */
public class TaskBeans {
	public static final String TASK_CONSUMER = "helium:TaskConsumer";
	public static final String TASK_PRODUCER_FACTORY = "helium:TaskProducerFactory";

	public static class PartitionBean {
		private int index ;
		private String name;
		private String desc;

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}
	}
}
