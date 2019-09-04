package org.helium.data.sharding.functions;

import org.helium.data.sharding.ShardingFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * 1. 先取模
 * 2. 按照物理的均分
 * Created by Coral on 7/25/16.
 */
public class ModDivFunction implements ShardingFunction<Long> {
	private String shardingFormat = "%.2d";
	private int modBy = 100;
	private int divBy = 10;

	@Override
	public int shardingLogical(Long key) {
		if (key >= 0) {
			return (int)(key % modBy);
		} else {
			return (int)(- key % modBy);
		}
	}

	@Override
	public int shardingPhysical(int logicalId) {
		return logicalId / divBy + 1;
	}

	@Override
	public String formatSharding(Long key) {
		int lid = shardingLogical(key);
		return String.format(shardingFormat, lid);
	}

	@Override
	public List<ShardingNode> getAllShardings() {
		List<ShardingNode> nodes = new ArrayList<>();
		for (int i = 0; i < modBy; i++) {
			ShardingNode node = new ShardingNode();
			node.setLogicalId(i);
			node.setPyshcalId(shardingPhysical(i));
			String name = String.format(shardingFormat, i);
			node.setShardingName(name);
			nodes.add(node);
		}
		return nodes;
	}
}
