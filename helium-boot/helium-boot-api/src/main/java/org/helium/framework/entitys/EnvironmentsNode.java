package org.helium.framework.entitys;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Childs;
import com.feinno.superpojo.annotation.Entity;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coral on 11/20/15.
 */
@Entity(name = "environments")
public class EnvironmentsNode extends SuperPojo {
	@Field(id = 1, name = "imports", type = NodeType.ATTR)
	private String imports;

	@Field(id = 2, name = "loader", type = NodeType.ATTR)
	private String loader;

	@Childs(id = 11, parent = "", child = "variable")
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
