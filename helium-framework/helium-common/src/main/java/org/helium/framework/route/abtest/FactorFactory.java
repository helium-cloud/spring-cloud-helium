package org.helium.framework.route.abtest;

import org.helium.framework.entitys.FactorGroupNode;
import org.helium.framework.entitys.FactorNode;
import org.helium.framework.route.abtest.FactorGroup.Condition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coral on 8/4/15.
 */
public class FactorFactory {
	public static final String CONDITION_NOT = "not";
	public static final String CONDITION_AND = "and";
	public static final String CONDITION_OR = "or";

	public static Factor createFrom(FactorGroupNode node) {
		List<Factor> list = new ArrayList<>();
		for (FactorGroupNode gn: node.getGroups()) {
			list.add(createFrom(gn));
		}
		for (FactorNode fn: node.getFactors()) {
			list.add(FactorImpl.createFactor(fn));
		}
		String condition = node.getCondition();
		if (CONDITION_AND.equals(condition)) {
			return new FactorGroup(Condition.AND, list, node.isDuplicate());
		} else if (CONDITION_OR.equals(condition)) {
			return new FactorGroup(Condition.OR, list, node.isDuplicate());
		} else if (CONDITION_NOT.equals(condition)) {
			if (list.size() != 1) {
				throw new IllegalArgumentException("only support 1 node when condition=not");
			}
			return new FactorGroup(Condition.NOT, list, node.isDuplicate());
		} else {
			throw new IllegalArgumentException("unknown condition:" + condition);
		}
	}


}
