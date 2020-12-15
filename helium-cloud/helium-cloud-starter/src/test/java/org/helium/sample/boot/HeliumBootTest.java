package org.helium.sample.boot;


import org.helium.framework.spring.annotation.EnableHeliumConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableHeliumConfiguration
@SpringBootApplication
public class HeliumBootTest {

    public static void main(String[] args) {
        SpringApplication.run(HeliumBootTest.class, args);
    }


}
