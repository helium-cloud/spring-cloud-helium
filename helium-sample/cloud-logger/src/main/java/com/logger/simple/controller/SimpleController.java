package com.logger.simple.controller;

import org.helium.cloud.logger.annotation.SystemLog;
import org.helium.cloud.logger.service.LogClient;
import org.helium.logging.args.LogArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("simple/")
public class SimpleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleController.class);


    @Autowired
    private LogClient logClient;

    @GetMapping(value = "ano")
    @SystemLog(businessType = "method-annotation-log")
    public List<Integer> testAno() {
		LOGGER.info("testAno");
        return IntStream.rangeClosed(0, 100).boxed().collect(Collectors.toList());

    }


	@GetMapping(value = "testClient")
	public List<Integer> testClient() {
		LOGGER.info("cli");
		logClient.log(LogArgs.createSimple(UUID.randomUUID().toString(), "1", "testClient"));
		return IntStream.rangeClosed(0, 100).boxed().collect(Collectors.toList());

	}

}
