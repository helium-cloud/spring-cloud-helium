package com.logger.simple.controller;

import org.helium.cloud.logger.annotation.SystemLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("simple")
public class SimpleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleController.class);

    @GetMapping
    @SystemLog(businessType = "method-annotation-log")
    public List<Integer> getList(int maxValue) {
        if (maxValue > 10) {
            LOGGER.error("maxValue[{}] error!", maxValue);
            throw new RuntimeException();
        }
        return IntStream.rangeClosed(0, maxValue).boxed().collect(Collectors.toList());

    }

}
