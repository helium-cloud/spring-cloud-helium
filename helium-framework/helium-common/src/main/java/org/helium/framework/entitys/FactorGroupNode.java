package org.helium.framework.entitys;

import org.helium.superpojo.SuperPojo;

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
public class FactorGroupNode extends SuperPojo {

	private String condition;

	private boolean duplicate;

	private int priority;


	private List<FactorGroupNode> groups = new ArrayList<>();

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

}




/*
<grayFactors condition="and">
	<factor key="id" operator="random" value="0.5"/>
</grayFactors>
 */
