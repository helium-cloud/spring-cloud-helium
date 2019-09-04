package org.helium.data.sharding.configuration;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Childs;
import com.feinno.superpojo.annotation.Entity;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;
import org.helium.framework.entitys.ObjectWithSettersNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coral on 7/15/16.
 */
@Entity(name = "ShardedDataSource")
public class ShardedDataSourceConfiguration extends SuperPojo {
	@Field(id = 1, name = "name", type = NodeType.ATTR)
	private String name;

	@Childs(id = 2, child = "dataSource", parent = "dataSources")
	private List<DataSourceNode> dataSources = new ArrayList<>();

	@Field(id = 3, name = "shardingFunction", type = NodeType.NODE)
	private ObjectWithSettersNode shardingFunction;

	@Field(id = 4, name = "tableCreator", type = NodeType.NODE)
	private ObjectWithSettersNode shardingTableCreator;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DataSourceNode> getDataSources() {
		return dataSources;
	}

	public void setDataSources(List<DataSourceNode> dataSources) {
		this.dataSources = dataSources;
	}

	public ObjectWithSettersNode getShardingFunction() {
		return shardingFunction;
	}

	public void setShardingFunction(ObjectWithSettersNode shardingFunction) {
		this.shardingFunction = shardingFunction;
	}

	public ObjectWithSettersNode getShardingTableCreator() {
		return shardingTableCreator;
	}

	public void setShardingTableCreator(ObjectWithSettersNode shardingTableCreator) {
		this.shardingTableCreator = shardingTableCreator;
	}
}
