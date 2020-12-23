package com.logger.simple;

import org.helium.framework.spring.annotation.EnableLicenseConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableLicenseConfiguration
@SpringBootApplication
public class LoggerSimpleApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoggerSimpleApplication.class, args);
	}
}
