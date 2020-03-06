package org.helium.rpc.stack;


import org.helium.framework.annotations.FixedExecutor;
import org.helium.framework.entitys.TagNode;
import org.helium.framework.tag.Tag;
import org.helium.framework.tag.TagMode;
import org.helium.rpc.server.RpcServiceBootstrap;
import org.helium.threading.ExecutorFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.EnumSet;
import java.util.concurrent.Executor;

//import org.helium.framework.annotations.FixedExecutor;

/**
 * Created by Coral on 7/9/15.
 */
public class LegacyRpcServiceTag implements Tag {
	private String serviceName;
	private Object serviceObject;

	@Override
	public EnumSet<TagMode> getModes() {
		return EnumSet.of(TagMode.ON_START, TagMode.ON_STOP);
	}

	@Override
	public void initWithConfig(Object object, TagNode node) {
//		if (beanContext.getType() != BeanType.SERVICE) {
//			throw new IllegalArgumentException("Only Service can apply @LegacyRpcService");
//		}
		this.serviceObject = object;
		//TagNode
		this.serviceName = node.getInnerText();
	}

	@Override
	public void initWithAnnotation(Object object, Annotation annotation, AnnotatedElement element) {
		this.serviceObject = object;
		this.serviceName = ((LegacyRpcService)annotation).serviceName();
	}

	@Override
	public TagNode getTagNode() {
		TagNode node = TagNode.create(this.getClass(), this.serviceName);
		node.setClazz(this.getClass());
		return node;
	}

	@Override
	public void applyTag(TagMode mode) throws Exception {
		Class<?> serviceInterface = getServiceInterface(serviceObject);
		//
		// 如果存在FixedExecutor注解,则建立Executor线程池,
		Annotation anno = serviceObject.getClass().getAnnotation(FixedExecutor.class);
		Executor executor = null;
		if (anno != null) {
			FixedExecutor anno2 = (FixedExecutor)anno;
			executor = ExecutorFactory.newFixedExecutor(anno2.name(), anno2.size(), anno2.limit());
		}
		LegacyRpcDecorator decorator = LegacyRpcDecorator.create(serviceName, serviceObject, serviceInterface, executor);
		RpcServiceBootstrap.INSTANCE.registerService(decorator);
	}

	private Class<?> getServiceInterface(Object object) {
		for (Class<?> intf: object.getClass().getInterfaces()) {
			if (intf.getAnnotation(LegacyRpcServiceInterface.class) != null) {
				return intf;
			}
		}
		throw new IllegalArgumentException("need @LegacyRpcServiceInterface on interfaces");
	}
}
