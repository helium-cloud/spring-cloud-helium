package org.helium.framework.service;

import org.helium.framework.annotations.FieldLoaderType;

/**
 * Created by Coral on 9/10/15.
 */
@FieldLoaderType(loaderType = HashedServiceLoader.class)
public interface HashedService<E> {
	E get(Object tag);
}
