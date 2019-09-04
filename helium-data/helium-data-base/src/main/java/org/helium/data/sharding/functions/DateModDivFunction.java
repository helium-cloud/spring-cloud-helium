package org.helium.data.sharding.functions;

import org.helium.data.sharding.ShardingFunction;

import java.util.Date;
import java.util.List;

/**
 * Created by Coral on 7/25/16.
 */
public class DateModDivFunction implements ShardingFunction<DateModDivFunction.ShardingKey> {

	private String shardingFormat;

	private int logicalShardings;
	private int pyshcalShardings;


	@Override
	public int shardingLogical(ShardingKey key) {
		return 0;
	}

	@Override
	public int shardingPhysical(int logicalId) {
		return 0;
	}


	@Override
	public String formatSharding(ShardingKey key) {
		return null;
	}

	@Override
	public List<ShardingNode> getAllShardings() {
		return null;
	}

	/**
	 * 分区Key
	 */
	public interface ShardingKey {
		long getKey();

		Date getDate();
	}
}
