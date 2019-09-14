package org.helium.cloud.simple.configcenter.controller;


import org.helium.framework.annotations.FieldSetter;
import org.helium.kafka.UkProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/kafka")
public class KafkaConfigController {

    @FieldSetter(value = "testkafka")
    private UkProducer ukProducer;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConfigController.class);

    @GetMapping("/test")
    @ResponseBody
    public String valueTest(){
        //http://127.0.0.1:8012/valuetest1
        ukProducer.produce(new String("{'biz': '1'}").getBytes());
        LOGGER.info("testPro:");
        return "testPro:";
    }



}