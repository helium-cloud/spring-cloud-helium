package org.helium.framework.servlet;

import org.helium.framework.entitys.ServletMappingsNode;

import java.lang.annotation.Annotation;

/**
 * 用于处理ServletMappings
 * Created by Coral on 7/7/15.
 */
public interface ServletMappings {
	/**
	 * 从配置进行初始化
	 * @param node
	 */
	void initWithConfig(ServletMappingsNode node);

	/**
	 * 通过批注初始化
	 * @param a
	 */
	void initWithAnnotation(Annotation a);

	/**
	 * 得到配置节点
	 * @param
	 * @return
	 */
	ServletMappingsNode getMappingsNode();

	/**
	 * 判断是否匹配
	 * @param args
	 * @return
	 */
	ServletMatchResult match(ServletMatchResult.Filter filter, Object... args);
//
//	/**
//	 * 默认的
//	 * @param args
//	 * @return
//	 */
//	default ServletMatchResult match(Object... args) {
//		return match(ServletMatchResult.ALL_FILTER, args);
//	}
}
