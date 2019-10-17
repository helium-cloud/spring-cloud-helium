package org.helium.data.sharding;

import org.helium.framework.annotations.FieldLoaderType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Coral on 7/15/16.
 */
@FieldLoaderType(loaderType = ShardedDataSourceLoader.class)
public abstract class ShardedDataSource<SK, SOURCE> {
	private ShardingFunction<SK> sf;
	private Map<Integer, SOURCE> sources = new HashMap<Integer, SOURCE>();

	void setShardingFunction(ShardingFunction<SK> sf) {
		this.sf = sf;
	}

	void addDataSource(int id, String dsName) {
		SOURCE source = loadDataSource(dsName);
		sources.put(id, source);
	}

    void addDataSource(int id, String dsName, String value) {
        SOURCE source = loadDataSource(dsName, value);
        sources.put(id, source);
    }

	protected abstract SOURCE loadDataSource(String dsName);
    protected abstract SOURCE loadDataSource(String dsName, String value);
	protected abstract SOURCE getSharding(SOURCE source, String shardingName);

	public SOURCE getSharding(SK shardingKey) {
		int lid = sf.shardingLogical(shardingKey);
		int pid = sf.shardingPhysical(lid);
		SOURCE source = sources.get(pid);

		String shardingName = sf.formatSharding(shardingKey);
		return getSharding(source, shardingName);
	}

	public List<SOURCE> getAllShardings() {
		List<SOURCE> list = new ArrayList<SOURCE>();
		for (ShardingFunction.ShardingNode node: sf.getAllShardings()) {
			SOURCE source = sources.get(node.getPyshcalId());
			SOURCE sharding = getSharding(source, node.getShardingName());
			list.add(sharding);
		}
		return list;
	}
}
