package org.helium.framework.route.abtest;

import org.helium.framework.module.ModuleContext;

/**
 * Created by Coral on 8/4/15.
 */
public interface Factor {
	boolean apply(ModuleContext ctx);
	default boolean duplicate(){return false;}
	@Override
	String toString();
}
