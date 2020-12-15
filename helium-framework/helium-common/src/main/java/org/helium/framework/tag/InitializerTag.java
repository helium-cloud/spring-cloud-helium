package org.helium.framework.tag;

import org.helium.framework.entitys.TagNode;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.EnumSet;

/**
 * Created by Coral on 7/4/15.
 */
public class InitializerTag implements Tag {
	private Method method;
	private Object object;

	@Override
	public EnumSet<TagMode> getModes() {
		return EnumSet.of(TagMode.ON_START);
	}

	@Override
	public void initWithConfig(Object object, TagNode node) {
		this.object = object;
		String methodName = node.getInnerText();
		for (Method method: object.getClass().getMethods()) {
			if (methodName.equals(method.getName())) {
				this.method = method;
			}
		}
		if (this.method == null) {
			throw new IllegalArgumentException("Initializer methodNotFound:" + methodName);
		}
	}

	@Override
	public void initWithAnnotation(Object object, Annotation annotation, AnnotatedElement element) {
		if (element instanceof Method) {
			this.object = object;
			this.method = (Method)element;
		} else {
			throw new IllegalArgumentException("@Initializer reflect failed:" + element.toString());
		}
	}

	@Override
	public TagNode getTagNode() {
		return TagNode.create(this.getClass(), this.method.getName());
	}

	@Override
	public void applyTag(TagMode mode) throws Exception {
		method.invoke(object);
	}
}
