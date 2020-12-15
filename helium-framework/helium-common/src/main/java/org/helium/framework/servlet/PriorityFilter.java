package org.helium.framework.servlet;


/**
 * 为了诡异的IFC逻辑，设计的IFC过滤器, 可以考虑挪出去了
 * 规则，寻找大于priority，并且最接近的值
 * 因为很重要，所以要过滤两遍
 *
 * Created by Coral on 8/10/15.
 */
public abstract class PriorityFilter<E extends ServletMappings> implements ServletMatchResult.Filter<E> {
	private int inputPriority;
	private int targetPriority;

	public int getInputPriority() {
		return inputPriority;
	}

	public int getTargetPriority() {
		return targetPriority;
	}

	public PriorityFilter(int priority) {
		inputPriority = priority;
		targetPriority = ServletMatchResult.DEFAULT_PRIORITY;
	}

	@Override
	public boolean applyFirst(ServletMatchResult mr) {
		if (mr.getPriority() <= inputPriority) {
			return false;
		} else {
			// 寻找最接近的值
			if (targetPriority > mr.getPriority()) {
				targetPriority = mr.getPriority();
			}
			return true;
		}
	}

	@Override
	public boolean applyLast(ServletMatchResult mr) {
		return mr.getPriority() <= targetPriority;
	}
}
