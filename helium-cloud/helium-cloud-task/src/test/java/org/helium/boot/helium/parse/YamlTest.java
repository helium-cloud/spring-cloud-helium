//package org.helium.boot.helium.parse;
//
//import com.alibaba.fastjson.JSONObject;
//import org.helium.boot.spring.assembly.HeliumTaskConfig;
//import org.junit.Assert;
//import org.junit.Test;
//import org.yaml.snakeyaml.Yaml;
//import sun.jvm.hotspot.debugger.Address;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.util.List;
//
//public class YamlTest {
//	public static void main(String[] args) throws Exception {
//		loadConfig();
//	}
//	public static void loadConfig() throws Exception {
//		String file = "/Users/wuhao/data/code/gitfeinno/helium/helium/helium-boot/helium-boot-starter-spring/src/test/resources/application.yml";
//		Yaml yaml = new Yaml();
//		HeliumTaskConfig heliumConfig = yaml.loadAs(new FileInputStream(file), HeliumTaskConfig.class);
//		System.out.println(heliumConfig.getId());
//		//Assert.assertEquals("111",heliumConfig.getId(), true);
//	}
//}
