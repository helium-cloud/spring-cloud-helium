package org.helium.framework.entitys;

import org.helium.superpojo.SuperPojo;

import java.util.List;

/**
 * <configurator>
 *     <setters>
 *         <setter ...></setter>
 *     </setters>
 * </configurator>
 * Created by Coral on 8/6/15.
 */
public class ConfiguratorConfiguration extends SuperPojo {
	private List<SetterNode> setters;

	public List<SetterNode> getSetters() {
		return setters;
	}

	public void setSetters(List<SetterNode> setters) {
		this.setters = setters;
	}
}
