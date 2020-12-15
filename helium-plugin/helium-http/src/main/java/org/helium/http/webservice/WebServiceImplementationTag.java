package org.helium.http.webservice;

import org.helium.framework.BeanContext;
import org.helium.framework.entitys.TagNode;
import org.helium.framework.servlet.ServletStack;
import org.helium.framework.servlet.StackManager;
import org.helium.framework.tag.Tag;
import org.helium.framework.tag.TagMode;
import org.helium.http.servlet.HttpServletStack;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.EnumSet;

/**
 * Created by Lei Gao on 8/25/15.
 */
public class WebServiceImplementationTag implements Tag {
	// private String stacks;
	private Object wsObject;
	private String wsPath;

	@Override
	public EnumSet<TagMode> getModes() {
		return EnumSet.of(TagMode.ON_START, TagMode.ON_STOP);
	}

	@Override
	public void initWithConfig(Object object, TagNode node) {
		wsObject = object;
		wsPath = node.getInnerText();
	}

	@Override
	public void initWithAnnotation(Object object, Annotation annotation, AnnotatedElement element) {
		wsObject = object;
		WebServiceImplementation wsAnno = (WebServiceImplementation)annotation;
		wsPath = wsAnno.value();
		// stacks = wsAnno.stacks();
	}

	@Override
	public TagNode getTagNode() {
		return TagNode.create(this.getClass(), wsPath);
	}

	@Override
	public void applyTag(TagMode mode) throws Exception {
		StackManager scs = BeanContext.getContextService().getService(StackManager.class);
		// for (String stackId: StrUtils.split(stacks, ",")) {

		for (ServletStack s: scs.getStacks()) {
			if (s instanceof HttpServletStack) {
				HttpServletStack stack = (HttpServletStack)s;
				if (mode == TagMode.ON_START) {
					stack.registerWebService(wsObject, wsPath);
				} else if (mode == TagMode.ON_STOP) {
					stack.unregisterWebService(wsPath);
				}
			}
		}
	}
}
