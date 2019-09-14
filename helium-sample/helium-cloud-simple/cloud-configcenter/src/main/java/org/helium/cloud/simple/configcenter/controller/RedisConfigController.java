package org.helium.cloud.simple.configcenter.controller;

import org.helium.framework.annotations.FieldSetter;
import org.helium.redis.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/redis")
public class RedisConfigController {
    @FieldSetter(value = "testredis")
    private RedisClient redisClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisConfigController.class);

    @GetMapping("/test")
    @ResponseBody
    public String valueTest(){
        LOGGER.info("valueTest:{}", redisClient.set("valueTest" , "valueTest"));
        LOGGER.info("valueTest:{}", redisClient.get("valueTest" ));
        return "valueTest:" + redisClient.get("valueTest" ) ;
    }



}