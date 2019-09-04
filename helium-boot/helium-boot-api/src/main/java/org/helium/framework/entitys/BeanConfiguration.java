package org.helium.framework.entitys;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Childs;
import com.feinno.superpojo.annotation.Entity;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * bean.xml
 * Created by Coral on 5/6/15.
 */
@Entity(name = "bean")
public class BeanConfiguration extends SuperPojo {
	@Field(id = 1, name = "type", type = NodeType.ATTR)
	private String type;

	@Field(id = 2, name = "id", type = NodeType.ATTR)
	private String id;

	@Field(id = 3, name = "export", type = NodeType.ATTR)
	private String export;

	/**
	 * for Task
	 */
	@Field(id = 4, name = "event", type = NodeType.NODE)
	private String event;

	/**
	 * for Service
	 */
	@Field(id = 5, name = "interface", type = NodeType.NODE)
	private String interfaceType;

	/**
	 * for Configurator
	 */
	@Field(id = 6, name = "path", type = NodeType.NODE)
	private String path;

	@Field(id = 7, name = "executor", type = NodeType.NODE)
	private ExecutorNode executor;

	@Field(id = 11, name = "object", type = NodeType.NODE)
	private ObjectWithSettersNode object;

	@Childs(id = 12, parent = "tags", child = "tag")
	private List<TagNode> tags = new ArrayList<>();

	@Childs(id = 13, parent = "modules", child = "module")
	private List<ObjectWithSettersNode> modules = new ArrayList<>();

	@Field(id = 14, name = "servletMappings", type = NodeType.NODE)
	private ServletMappingsNode servletMappings;

	@Childs(id = 15, parent = "extensions", child = "extension")
	private List<KeyValueNode> extensions = new ArrayList<>();

	@Field(id = 21, name = "parentNode", type = NodeType.NODE)
	private BeanNode parentNode;

	@Field(id = 22, name = "adatperTag", type = NodeType.NODE)
	private String adatperTag;

	@Field(id = 23, name = "priority", type = NodeType.NODE)
	private int priority;

	@Field(id = 24, name = "storageType", type = NodeType.NODE)
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
