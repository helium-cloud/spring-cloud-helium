package org.helium.framework.entitys;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Childs;
import com.feinno.superpojo.annotation.Entity;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * bootstrap.xml
 * Created by Coral on 5/14/15.
 */
@Entity(name="bootstrap")
public class BootstrapConfiguration extends SuperPojo {
	@Field(id = 1, name = "id", type = NodeType.ATTR)
	private String id;

	@Field(id = 2, name = "environments", type = NodeType.NODE)
	private EnvironmentsNode environmentsNode;

	@Field(id = 3, name = "centralizedService", type = NodeType.NODE)
	private ObjectWithSettersNode centralizedService;

	@Childs(id = 11, parent = "executors", child = "executor")
	private List<ExecutorNode> executors = new ArrayList<>();

	@Childs(id = 12, parent = "stacks", child = "stack")
	private List<ObjectWithSettersNode> stacks = new ArrayList<>();

	@Childs(id = 13, parent = "beans", child = "bean")
	private List<BeanNode> beans = new ArrayList<>();

	@Childs(id = 14, parent = "references", child = "reference")
	private List<BeanReferenceNode> references = new ArrayList<>();

	@Childs(id = 15, parent = "bundles", child = "bundle")
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
