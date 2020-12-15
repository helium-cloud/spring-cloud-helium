package org.helium.framework.tag;

import org.helium.framework.entitys.TagNode;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.EnumSet;

/**
 * 配置于任何一个可工作对象上的Tag
 * Created by Coral on 7/4/15.
 */
public interface Tag {
	/**
	 * 获取Tag的工作模式
	 * @return
	 */
	EnumSet<TagMode> getModes();

	/**
	 * 使用Config进行初始化
	 * @param object
	 * @param node
	 */
	void initWithConfig(Object object, TagNode node);

	/**
	 * 使用反射进行初始化
	 * @return
	 */
	void initWithAnnotation(Object object, Annotation annotation, AnnotatedElement element);

	/**
	 * 获取配置节点
	 * @return
	 */
	TagNode getTagNode();

	/**
	 *
	 * @throws Exception
	 */
	void applyTag(TagMode mode) throws Exception;
}
