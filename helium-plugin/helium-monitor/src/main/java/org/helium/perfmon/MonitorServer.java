package org.helium.perfmon;

import org.helium.perfmon.simple.PerfmonCounters;
import org.springframework.boot.SpringApplication;

import java.util.Properties;

public class MonitorServer {
	public static void run(int port) {
		PerfmonCounters.getInstance("history");
		SpringApplication springApplication = new SpringApplication(MonitorApplication.class);
		Properties configProperties = new Properties();
		configProperties.setProperty("server.port", String.valueOf(port));
		springApplication.setDefaultProperties(configProperties);
		springApplication.run();


	}
}
