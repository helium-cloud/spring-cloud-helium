package org.helium.framework.entitys;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Childs;
import com.feinno.superpojo.annotation.Entity;

import java.util.List;

/**
 * Created by Coral on 10/15/15.
 */
@Entity(name = "entrys")
public class MapConfiguration extends SuperPojo {
	@Childs(id = 1, parent = "", child = "entry")
	private List<KeyValueNode> entrys;

	public List<KeyValueNode> getEntrys() {
		return entrys;
	}

	public void setEntrys(List<KeyValueNode> entrys) {
		this.entrys = entrys;
	}
}
