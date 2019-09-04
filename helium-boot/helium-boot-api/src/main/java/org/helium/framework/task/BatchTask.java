package org.helium.framework.task;

import java.util.List;

/**
 * 以下几种场景的基础类
 * 1. 适用用并发场景较高情况：单条可转批量情况
 */
public interface BatchTask<E> {
	void processTask(List<E> argList);
}
