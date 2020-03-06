package org.helium.sample.boot.test;

import org.helium.framework.spring.assembly.HeliumConfig;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;

public class YamlTest {
	public static void main(String[] args) throws Exception {
		loadConfig();
	}
	public static void loadConfig() throws Exception {
		String file = "/Users/wuhao/data/code/gitfeinno/helium/helium/helium-boot/helium-boot-starter-spring/src/test/resources/application.yml";
		Yaml yaml = new Yaml();
		HeliumConfig heliumConfig = yaml.loadAs(new FileInputStream(file), HeliumConfig.class);
		System.out.println(heliumConfig.getId());
		//Assert.assertEquals("111",heliumConfig.getId(), true);
	}
}
