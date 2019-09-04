package org.helium.sample.bootstrap.advanced;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Entity;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

/**
 * Created by Coral on 7/13/17.
 */
@Entity(name = "sample-config")
public class SampleConfigXml extends SuperPojo {
	@Field(id = 1, name = "attr1", type = NodeType.ATTR)
	private String attr1;
	
	@Field(id = 2, name = "consumer", type = NodeType.NODE)
	private String node1;

	public String getAttr1() {
		return attr1;
	}

	public void setAttr1(String attr1) {
		this.attr1 = attr1;
	}

	public String getNode1() {
		return node1;
	}

	public void setNode1(String node1) {
		this.node1 = node1;
	}
}
