package org.helium.sample.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class TaskApp1Application {

    public static void main(String[] args) {
        SpringApplication.run(TaskApp1Application.class, args);
    }


}
