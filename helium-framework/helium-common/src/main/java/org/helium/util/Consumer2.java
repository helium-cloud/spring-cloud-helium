package org.helium.util;

/**
 * Created by Coral on 1/10/17.
 */
@FunctionalInterface
public interface Consumer2<E1, E2> {
	void apply(E1 obj1, E2 obj2);
}
