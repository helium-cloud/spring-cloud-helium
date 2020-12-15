package org.helium.framework.route.abtest;

import org.helium.framework.module.ModuleContext;

import java.util.List;

/**
 * Created by Coral on 8/6/15.
 */
public class FactorGroup implements Factor {
	enum Condition {
		AND,
		OR,
		NOT,
	}
	private boolean duplicate;
	private Condition condition;
	private List<Factor> factors;

	FactorGroup(Condition condition, List<Factor> factors, boolean duplicate) {
		this.condition = condition;
		this.factors = factors;
		this.duplicate = duplicate;
	}

	FactorGroup(Condition condition, List<Factor> factors) {
		this.condition = condition;
		this.factors = factors;
	}

	@Override
	public boolean apply(ModuleContext ctx) {
		switch (condition) {
			case AND:
				for (Factor factor: factors) {
					if (!factor.apply(ctx)) {
						return false;
					}
				}
				return true;
			case OR:
				if (factors.size() == 0){
					return true;
				}
				for (Factor factor: factors) {
					if (factor.apply(ctx)) {
						return true;
					}
				}
				return false;
			case NOT:
				return !factors.get(0).apply(ctx);
		}
		throw new IllegalArgumentException("Unexpected condition:" + condition);
	}
	@Override
	public boolean duplicate() {
		return duplicate;
	}

	public List<Factor> getFactors() {
		return factors;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(condition.name());
		for (int i = 0; i < factors.size(); i++) {
			str.append(i == 0 ? "(" : ",");
			str.append(factors.get(i).toString());
		}
		str.append(")");
		return str.toString();
	}
}
