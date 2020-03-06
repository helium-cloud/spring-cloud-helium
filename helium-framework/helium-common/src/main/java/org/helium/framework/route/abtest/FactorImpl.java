package org.helium.framework.route.abtest;

import org.helium.framework.entitys.FactorNode;
import org.helium.framework.module.ModuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * Created by Coral on 8/6/15.
 */
class FactorImpl implements Factor {
	private static final Logger LOGGER = LoggerFactory.getLogger(FactorImpl.class);

	public static final String OP_EQUALS = "equals";
	public static final String OP_GT = "gt";
	public static final String OP_LT = "lt";
	public static final String OP_REGEX = "regex";
	// public static final String OP_PACKAGE = "package";

	private String key;
	private String operator;
	private String value;
	private Function<String, Boolean> judgement;

	FactorImpl(String key, String operator, String value, Function<String, Boolean> judgement) {
		this.key = key;
		this.operator = operator;
		this.judgement = judgement;
	}

	@Override
	public boolean apply(ModuleContext ctx) {
		Object data = ctx.getModuleData(key);
		if (data == null) {
			return false;   // 如果缺少此字段则默认不命中
		} else {
			try {
				return judgement.apply(data.toString());
			} catch (Exception ex) {
				LOGGER.error("base judgement for: '{}'", data.toString());
				return false;
			}
		}
	}

	@Override
	public String toString() {
		return key + " " + operator + " " + value;
	}

	public static Factor createFactor(FactorNode node) {
		String key = node.getKey();
		String value = node.getValue();
		String op = node.getOperator().toLowerCase();
		if (OP_EQUALS.equals(op)) {
			return new FactorImpl(node.getKey(), op, value, (v) -> (value.equals(v)));
		} else if (OP_GT.equals(op)) {
			return new FactorImpl(node.getKey(), op, value,  (v) -> (Double.parseDouble(v) + DELTA >= Double.parseDouble(value)));
		} else if (OP_LT.equals(op)) {
			return new FactorImpl(node.getKey(), op, value,  (v) -> (Double.parseDouble(v) < Double.parseDouble(value)));
		} else if (OP_REGEX.equals(op)) {
			return new RegexFactorImpl(node.getKey(), value);
		} else {
			throw new IllegalArgumentException("unknown operator:" + op);
		}
	}



	private static final double DELTA = 1E-9;
}
