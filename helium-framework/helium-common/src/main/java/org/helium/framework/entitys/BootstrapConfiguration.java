package org.helium.framework.entitys;


import org.helium.superpojo.SuperPojo;

import java.util.ArrayList;
import java.util.List;

/**
 * bootstrap.xml
 * Created by Coral on 5/14/15.
 */

public class BootstrapConfiguration extends SuperPojo {

	private String id;

	private EnvironmentsNode environmentsNode;


	private ObjectWithSettersNode centralizedService;

	private List<ExecutorNode> executors = new ArrayList<>();


	private List<ObjectWithSettersNode> stacks = new ArrayList<>();


	private List<BeanNode> beans = new ArrayList<>();


	private List<BeanReferenceNode> references = new ArrayList<>();

	private List<BundleNode> bundles = new ArrayList<>();

	public List<KeyValueNode> getEnvironments() {
		return environmentsNode.getVariables();
	}


	public List<ObjectWithSettersNode> getStacks() {
		return stacks;
	}

	public void setStacks(List<ObjectWithSettersNode> stacks) {
		this.stacks = stacks;
	}

	public List<BeanNode> getBeans() {
		return beans;
	}

	public void setBeans(List<BeanNode> beans) {
		this.beans = beans;
	}

	public List<BundleNode> getBundles() {
		return bundles;
	}

	public void setBundles(List<BundleNode> bundles) {
		this.bundles = bundles;
	}

	public List<BeanReferenceNode> getReferences() {
		return references;
	}

	public void setReferences(List<BeanReferenceNode> references) {
		this.references = references;
	}

	public ObjectWithSettersNode getCentralizedService() {
		return centralizedService;
	}

	public void setCentralizedService(ObjectWithSettersNode centralizedService) {
		this.centralizedService = centralizedService;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public EnvironmentsNode getEnvironmentsNode() {
		return environmentsNode;
	}

	public void setEnvironmentsNode(EnvironmentsNode environmentsNode) {
		this.environmentsNode = environmentsNode;
	}

	public List<ExecutorNode> getExecutors() {
		return executors;
	}

	public void setExecutors(List<ExecutorNode> executors) {
		this.executors = executors;
	}
}
