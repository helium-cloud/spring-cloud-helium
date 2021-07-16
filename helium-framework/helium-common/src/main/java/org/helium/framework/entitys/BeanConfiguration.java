package org.helium.framework.entitys;


import org.helium.superpojo.SuperPojo;

import java.util.ArrayList;
import java.util.List;

/**
 * bean.xml
 * Created by Coral on 5/6/15.
 */
public class BeanConfiguration extends SuperPojo {

	private String type;

	private String id;

	private String export;

	/**
	 * for Task
	 */
	private String event;

	/**
	 * for Service
	 */
	private String interfaceType;

	/**
	 * for Configurator
	 */
	private String path;

	private ExecutorNode executor;

	private ObjectWithSettersNode object;

	private List<TagNode> tags = new ArrayList<>();

	private List<ObjectWithSettersNode> modules = new ArrayList<>();

	private ServletMappingsNode servletMappings;

	private List<KeyValueNode> extensions = new ArrayList<>();

	private BeanNode parentNode;

	private String adatperTag;

	private int priority;

	private String storageType;


	public void addExtension(KeyValueNode e) {
		extensions.add(e);
	}

	public String getExtension(String key) {
		for (KeyValueNode e : extensions) {
			if (e.getKey().equals(key)) {
				return e.getValue();
			}
		}
		return null;
	}

	private Class<?> interfaceClazz;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ObjectWithSettersNode getObject() {
		return object;
	}

	public void setObject(ObjectWithSettersNode object) {
		this.object = object;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getInterfaceType() {
		return interfaceType;
	}

	public void setInterfaceType(String interfaceType) {
		this.interfaceType = interfaceType;
	}

	public List<TagNode> getTags() {
		return tags;
	}

	public void setTags(List<TagNode> tags) {
		this.tags = tags;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<ObjectWithSettersNode> getModules() {
		return modules;
	}

	public void setModules(List<ObjectWithSettersNode> modules) {
		this.modules = modules;
	}

	public ServletMappingsNode getServletMappings() {
		return servletMappings;
	}

	public void setServletMappings(ServletMappingsNode servletMappings) {
		this.servletMappings = servletMappings;
	}

	public Class<?> getInterfaceClazz() {
		return interfaceClazz;
	}

	public void setInterfaceClazz(Class<?> interfaceClazz) {
		this.interfaceClazz = interfaceClazz;
	}

	public BeanNode getParentNode() {
		return parentNode;
	}

	public void setParentNode(BeanNode parentNode) {
		this.parentNode = parentNode;
	}

	public List<KeyValueNode> getExtensions() {
		return extensions;
	}

	public void setExtensions(List<KeyValueNode> extensions) {
		this.extensions = extensions;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public ExecutorNode getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorNode executor) {
		this.executor = executor;
	}

	public String getExport() {
		return export;
	}

	public void setExport(String export) {
		this.export = export;
	}

	public String getAdatperTag() {
		return adatperTag;
	}

	public void setAdatperTag(String adatperTag) {
		this.adatperTag = adatperTag;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getStorageType() {
		return storageType;
	}

	public void setStorageType(String storageType) {
		this.storageType = storageType;
	}

}
