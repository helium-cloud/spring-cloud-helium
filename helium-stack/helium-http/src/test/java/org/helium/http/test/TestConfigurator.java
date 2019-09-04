package org.helium.http.test;

import org.helium.framework.annotations.Configuration;
import org.helium.framework.configuration.Configurator;

/**
 * Created by Coral on 8/11/15.
 */
@Configuration(path = "test-config.xml")
public class TestConfigurator extends Configurator<TestConfigurator> {
	public static final TestConfigurator INSTANCE = new TestConfigurator();
	public TestConfigurator() {
		super(INSTANCE);
	}

	private String hello;
	private String hello2;

	public String getHello() {
		return hello;
	}

	public String getHello2() {
		return hello2;
	}

	@Override
	public void reloadValues() {
		hello2 = "fuck:" + hello;
	}
}
