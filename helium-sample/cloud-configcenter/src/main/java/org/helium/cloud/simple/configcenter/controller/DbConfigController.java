package org.helium.cloud.simple.configcenter.controller;


import org.helium.database.Database;
import org.helium.framework.annotations.FieldSetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/db")
public class DbConfigController {
    @FieldSetter(value = "testdb", group = "test")
    private Database database;

    private static final Logger LOGGER = LoggerFactory.getLogger(DbConfigController.class);

    @GetMapping("/test")
    @ResponseBody
    public String valueTest(){
        //http://127.0.0.1:8012/valuetest1
        LOGGER.info("testPro:{}", database.test());
        return "testPro:" + database.test() ;
    }


}