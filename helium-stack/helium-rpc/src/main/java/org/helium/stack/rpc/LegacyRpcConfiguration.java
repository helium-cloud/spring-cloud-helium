package org.helium.stack.rpc;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Childs;
import com.feinno.superpojo.annotation.Entity;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;
import org.helium.framework.entitys.KeyValueNode;

import java.util.List;

/**
 * Created by Coral on 6/15/15.
 */
@Entity(name="rpcService")
public class LegacyRpcConfiguration extends SuperPojo {
	@Field(id = 1, name = "service", type = NodeType.ATTR)
	private String service;

	@Childs(id = 2, parent = "endpoints", child="endpoint")
	private List<KeyValueNode> endpoints;

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public List<KeyValueNode> getEndpoints() {
		return endpoints;
	}

	public void setEndpoints(List<KeyValueNode> endpoints) {
		this.endpoints = endpoints;
	}
}
