package org.helium.framework.entitys;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.helium.superpojo.SuperPojo;
import org.helium.util.CollectionUtils;
import org.helium.util.StringUtils;
import org.helium.util.TypeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * bootstrap.xml, bundle.xml内的<bean/>节点
 * Created by Coral on 7/2/15.
 */
public final class BeanNode extends SuperPojo {

	private String path;

	private String clazz;

	private String mode;

	private String stacksAttr;

	private String enabled;

	private String export;

	private String id;

	private int loadOnStartup;

	private String executor;

	private List<SetterNode> setterNodes = new ArrayList<>();

	private String settersAttr;

	private Map<String, SetterNode> mergedSetters = null;

	public List<SetterNode> getSetters() throws JsonProcessingException {
		if (mergedSetters == null) {
			mergedSetters = mergeSetters();
		}
		return CollectionUtils.cloneValues(mergedSetters);
	}

	public List<String> getStacks() {
		Map<String, String> map = new HashMap<>();
		for (String s: TypeUtils.split(stacksAttr, ",")) {
			map.put(s, s);
		}
		return CollectionUtils.cloneKeys(map);
	}

	private Map<String, SetterNode> mergeSetters() throws JsonProcessingException {
		Map<String, SetterNode> nodes = new HashMap<>();
		for (SetterNode setter: setterNodes){
			nodes.put(setter.getField(), setter);
		}
		if (!StringUtils.isNullOrEmpty(settersAttr)) {
			Map<String, String> ss = StringUtils.splitValuePairs(settersAttr, ";", "=");
			for (Entry<String, String> e: ss.entrySet()) {
				if (nodes.get(e.getKey()) == null) {
					SetterNode node = SetterNode.create(e.getKey(), null, e.getValue());
					nodes.put(e.getKey(), node);
				}
			}
		}
		return nodes;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getStacksAttr() {
		return stacksAttr;
	}

	public void setStacksAttr(String stacksAttr) {
		this.stacksAttr = stacksAttr;
	}

	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}

	public String getExport() {
		return export;
	}

	public void setExport(String export) {
		this.export = export;
	}

	public String getSettersAttr() {
		return settersAttr;
	}

	public void setSettersAttr(String settersAttr) {
		this.settersAttr = settersAttr;
	}

	public List<SetterNode> getSetterNodes() {
		return setterNodes;
	}

	public void setSetterNodes(List<SetterNode> setterNodes) {
		this.setterNodes = setterNodes;
	}

	public String getMode() {
		return mode;
	}

	public BeanNode setMode(String mode) {
		this.mode = mode;
		return this;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 用于将<beans/>节点中的stacks属性合并到<bean/>节点当中
	 * @param stacks
	 */
	public void mergeStacks(String stacks) {
		if (stacks == null) {
			return;
		}
		if (!StringUtils.isNullOrEmpty(stacksAttr)) {
			stacksAttr = stacksAttr + "," + stacks;
		} else {
			stacksAttr = stacks;
		}
	}

	public int getLoadOnStartup() {
		return loadOnStartup;
	}

	public void setLoadOnStartup(int loadOnStartup) {
		this.loadOnStartup = loadOnStartup;
	}

	public String getExecutor() {
		return executor;
	}

	public void setExecutor(String executor) {
		this.executor = executor;
	}
}
