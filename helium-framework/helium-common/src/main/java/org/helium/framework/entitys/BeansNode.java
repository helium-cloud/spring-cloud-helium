package org.helium.framework.entitys;

import org.helium.superpojo.SuperPojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coral on 10/24/15.
 */
public class BeansNode extends SuperPojo {

	private String stacks;

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
