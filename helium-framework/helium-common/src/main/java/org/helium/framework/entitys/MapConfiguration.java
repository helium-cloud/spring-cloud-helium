package org.helium.framework.entitys;


import org.helium.superpojo.SuperPojo;

import java.util.List;

/**
 * Created by Coral on 10/15/15.
 */
public class MapConfiguration extends SuperPojo {
	private List<KeyValueNode> entrys;

	public List<KeyValueNode> getEntrys() {
		return entrys;
	}

	public void setEntrys(List<KeyValueNode> entrys) {
		this.entrys = entrys;
	}
}
