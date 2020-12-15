package org.helium.test.perfmon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class DashBoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(DashBoardApplication.class, args);
	}

}
