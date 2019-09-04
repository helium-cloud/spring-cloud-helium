package org.helium.sample.bootstrap.quickstart;

import org.helium.framework.annotations.ServiceImplementation;

/**
 * Created by Coral on 6/15/15.
 */
@ServiceImplementation
public class SampleBeanImpl implements SampleBean {
	private String Bonjour = "bonjour:";

	@Override
	public String hello(String name) {
		return Bonjour + name;
	}
}
