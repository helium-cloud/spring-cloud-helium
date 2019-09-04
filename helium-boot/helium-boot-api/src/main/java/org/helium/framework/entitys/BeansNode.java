package org.helium.framework.entitys;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Childs;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coral on 10/24/15.
 */
public class BeansNode extends SuperPojo {
	@Field(id = 1, name = "stacks", type = NodeType.ATTR)
	private String stacks;

	@Childs(id = 11, parent = "", child = "bean")
	private List<BeanNode> beans = new ArrayList<>();

	public String getStacks() {
		return stacks;
	}

	public void setStacks(String stacks) {
		this.stacks = stacks;
	}

	public List<BeanNode> getBeans() {
		return beans;
	}

	public void setBeans(List<BeanNode> beans) {
		this.beans = beans;
	}
}
