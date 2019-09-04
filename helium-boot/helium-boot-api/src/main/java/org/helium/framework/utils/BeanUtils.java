package org.helium.framework.utils;

import org.helium.framework.BeanIdentity;
import org.helium.framework.annotations.ServiceInterface;

/**
 * Created by Coral on 7/20/15.
 */
public class BeanUtils {
	public static BeanIdentity getId(Class<?> clazz) {
		ServiceInterface si = clazz.getAnnotation(ServiceInterface.class);
		if (si == null) {
			throw new IllegalArgumentException("getId need @ServiceInterface:" + clazz.getName());
		}
		return BeanIdentity.parseFrom(si.id());
	}
}
