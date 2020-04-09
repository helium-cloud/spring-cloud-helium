package org.helium.servlet;

import org.helium.framework.spring.annotation.EnableHeliumConfiguration;
import org.helium.framework.spring.autoconfigure.HeliumAutoWired;
import org.helium.servlet.servlet.Im5GSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * 类描述：TODO
 *
 * @author zkailiang
 * @date 2020/4/2
 */
@EnableHeliumConfiguration
@SpringBootApplication
public class Application {

	@Autowired
	private Im5GSmsService im5GSmsService;

	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(){
		return args -> {
			System.out.println(im5GSmsService.get());

		};
	}
}
