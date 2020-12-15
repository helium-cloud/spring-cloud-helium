package org.helium.framework.entitys;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Childs;
import com.feinno.superpojo.annotation.Entity;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * bundle.xml
 * Created by Coral on 7/2/15.
 */
@Entity(name = "bundle")
public class BundleConfiguration extends SuperPojo {
	@Field(id = 1, name = "name", type = NodeType.ATTR)
	private String name;

	@Field(id = 2, name = "beans", type = NodeType.NODE)
	private BeansNode beansNode;

	@Childs(id = 11, parent = "configImports", child = "configImport")
	private List<ConfigImportNode> configImports = new ArrayList<>();

	@Childs(id = 13, parent = "references", child = "reference")
	private List<BeanReferenceNode> references = new ArrayList<>();

	@Field(id = 21, name = "parentNode", type = NodeType.NODE)
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
