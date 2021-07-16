package org.helium.framework.entitys;


import org.helium.superpojo.SuperPojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coral on 11/20/15.
 */
public class EnvironmentsNode extends SuperPojo {

	private String imports;


	private String loader;

	private List<KeyValueNode> variables = new ArrayList<>();

	public String getImports() {
		return imports;
	}

	public void setImports(String imports) {
		this.imports = imports;
	}

	public String getLoader() {
		return loader;
	}

	public void setLoader(String loader) {
		this.loader = loader;
	}

	public List<KeyValueNode> getVariables() {
		return variables;
	}

	public void setVariables(List<KeyValueNode> variables) {
		this.variables = variables;
	}
}
