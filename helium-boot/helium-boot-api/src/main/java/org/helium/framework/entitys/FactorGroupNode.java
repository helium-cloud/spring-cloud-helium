package org.helium.framework.entitys;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Childs;
import com.feinno.superpojo.annotation.Entity;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * <grayFactors rule="and">
 *     <group condition="and"/>
 *      <factor/>
 *     </group>
 *   <grayFactor key="impu" operator="package" value=""/>
 *   <grayFactor key="impu" operator="random" value="" value=""/>
 * </grayFactors>
 * Created by Coral on 8/4/15.
 */
@Entity(name = "factors")
public class FactorGroupNode extends SuperPojo {
	@Field(id = 1, name = "condition", type = NodeType.ATTR)
	private String condition;

	@Field(id = 2, name = "duplicate", type = NodeType.ATTR)
	private boolean duplicate;

	@Field(id = 3, name = "priority", type = NodeType.ATTR)
	private int priority;

	@Childs(id = 11, parent = "", child = "group")
	private List<FactorGroupNode> groups = new ArrayList<>();

	@Childs(id = 12, parent = "", child = "factor")
	private List<FactorNode> factors = new ArrayList<>();

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean isDuplicate() {
		return duplicate;
	}

	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}

	public List<FactorGroupNode> getGroups() {
		return groups;
	}

	public void setGroups(List<FactorGroupNode> groups) {
		this.groups = groups;
	}

	public List<FactorNode> getFactors() {
		return factors;
	}

	public void setFactors(List<FactorNode> factors) {
		this.factors = factors;
	}

	public static void main(String[] args) {
		String xml = "<factors condition=\"and\" duplicate=\"true\">\n" +
				"\t<factor key=\"id\" operator=\"random\" value=\"0.5\"/>\n" +
				"</factors>";

		FactorGroupNode config = new FactorGroupNode();
		config.parseXmlFrom(xml);
		System.out.println(config.toString());

	}
}




/*
<grayFactors condition="and">
	<factor key="id" operator="random" value="0.5"/>
</grayFactors>
 */
