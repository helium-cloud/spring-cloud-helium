package org.helium.framework.utils;

/**
 * Created by Coral on 7/17/17.
 */
public interface Closeable<E> {
	E get();
	
	void close();
}
