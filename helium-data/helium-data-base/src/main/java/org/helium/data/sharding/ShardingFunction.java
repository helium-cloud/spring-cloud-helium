package org.helium.data.sharding;

import java.util.List;

/**
 * Created by Coral on 7/15/16.
 */
public interface ShardingFunction<E> {
	/**
	 * 从Key获取逻辑id
	 * @param key
	 * @return
	 */
	int shardingLogical(E key);

	/**
	 * 从logicalId获取physicalId
	 * @param logicalId
	 * @return
	 */
	int shardingPhysical(int logicalId);

	/**
	 * 格式化key
	 * @param key
	 * @return
	 */
	String formatSharding(E key);

	/**
	 * 获取所有可能的ShardingName
	 * @return
	 */
	List<ShardingNode> getAllShardings();

	/**
	 * 用来描述
	 */
	class ShardingNode {
		private int logicalId;
		private int pyshcalId;
		private String shardingName;

		public int getLogicalId() {
			return logicalId;
		}

		public void setLogicalId(int logicalId) {
			this.logicalId = logicalId;
		}

		public int getPyshcalId() {
			return pyshcalId;
		}

		public void setPyshcalId(int pyshcalId) {
			this.pyshcalId = pyshcalId;
		}

		public String getShardingName() {
			return shardingName;
		}

		public void setShardingName(String shardingName) {
			this.shardingName = shardingName;
		}
	}
}
