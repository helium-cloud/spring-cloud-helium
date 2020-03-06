package org.helium.framework.entitys;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Childs;
import com.feinno.superpojo.annotation.Entity;

import java.util.List;

/**
 * <configurator>
 *     <setters>
 *         <setter ...></setter>
 *     </setters>
 * </configurator>
 * Created by Coral on 8/6/15.
 */
@Entity(name = "configurator")
public class ConfiguratorConfiguration extends SuperPojo {
	@Childs(id = 1, parent = "setters", child = "setter")
	private List<SetterNode> setters;

	public List<SetterNode> getSetters() {
		return setters;
	}

	public void setSetters(List<SetterNode> setters) {
		this.setters = setters;
	}
}
