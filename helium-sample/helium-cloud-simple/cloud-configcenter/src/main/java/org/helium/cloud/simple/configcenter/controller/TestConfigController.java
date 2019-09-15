package org.helium.cloud.simple.configcenter.controller;


import org.helium.cloud.configcenter.ConfigCenterClient;

import com.feinno.superpojo.util.StringUtils;
import org.helium.framework.annotations.FieldSetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/test")
public class TestConfigController {
    @Autowired
    private ConfigCenterClient configCenterClient;

    @FieldSetter(value = "teststr", group = "test")
    private String testStr;

    @FieldSetter(value = "testkey", group = "other")
    private String testKey;


    private static final Logger LOGGER = LoggerFactory.getLogger(TestConfigController.class);

    @GetMapping("/cur")
    @ResponseBody
    public String test(@RequestParam(required = false) String group, @RequestParam(required = false) String key){
        //http://127.0.0.1:8012/test?group=dubbo&key=appname
        //http://127.0.0.1:8012/test?key=commonDistName
        String value = (String) configCenterClient.get(key, group);
        StringBuilder sb = new StringBuilder();
        sb.append("group:").append(group);
        sb.append("test:").append(key);
        if (StringUtils.isNullOrEmpty(value)){
            sb.append("  value:").append("key is null");
        } else {
            sb.append("  value:").append(value);
        }
        LOGGER.info("print:{}", sb.toString());
        return sb.toString() ;
    }

    @GetMapping("/test")
    @ResponseBody
    public String valueTest(){
		LOGGER.info("teststr:{}", testStr);
		return "teststr:" + testStr ;
    }

    @GetMapping("/testkey")
    @ResponseBody
    public String valueTest1(){
        //http://127.0.0.1:8012/valuetest1
        LOGGER.info("testkey:{}", testKey);
        return "testPro:" + testKey ;
    }


}