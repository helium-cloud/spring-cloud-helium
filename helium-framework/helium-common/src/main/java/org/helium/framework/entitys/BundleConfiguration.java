package org.helium.framework.entitys;


import org.helium.superpojo.SuperPojo;

import java.util.ArrayList;
import java.util.List;

/**
 * bundle.xml
 * Created by Coral on 7/2/15.
 */

public class BundleConfiguration extends SuperPojo {
	private String name;

	private BeansNode beansNode;

	private List<ConfigImportNode> configImports = new ArrayList<>();

	private List<BeanReferenceNode> references = new ArrayList<>();

	private BundleNode parentNode;

	public List<ConfigImportNode> getConfigImports() {
		return configImports;
	}

	public void setConfigImports(List<ConfigImportNode> configImports) {
		this.configImports = configImports;
	}

	public List<BeanNode> getBeans() {
		return beansNode.getBeans();
	}

	public void setBeans(List<BeanNode> beans) {
		if (beansNode == null) {
			beansNode = new BeansNode();
		}
		beansNode.setBeans(beans);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BundleNode getParentNode() {
		return parentNode;
	}

	public BundleConfiguration setParentNode(BundleNode parentNode) {
		this.parentNode = parentNode;
		return this;
	}

	public List<BeanReferenceNode> getReferences() {
		return references;
	}

	public void setReferences(List<BeanReferenceNode> references) {
		this.references = references;
	}

	public BeansNode getBeansNode() {
		return beansNode;
	}

	public void setBeansNode(BeansNode beansNode) {
		this.beansNode = beansNode;
	}
}
