package org.helium.cloud.simple.configcenter.controller;


import org.helium.database.DataRow;
import org.helium.database.DataTable;
import org.helium.database.Database;
import org.helium.framework.annotations.FieldSetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;


@Controller
@RequestMapping("/db")
public class DbConfigController {
    @FieldSetter(value = "testdb")
    private Database database;

    private static final Logger LOGGER = LoggerFactory.getLogger(DbConfigController.class);

    @GetMapping("/test")
    @ResponseBody
    public String valueTest(){
        //http://127.0.0.1:8012/valuetest1
        LOGGER.info("testPro:{}", database.test());
        return "testPro:" + database.test() ;
    }

    @GetMapping("/select")
    @ResponseBody
    public String valueTest1(){
        //http://127.0.0.1:8012/valuetest
        StringBuilder stringBuilder = new StringBuilder();
        try {
            DataTable dataTable = database.executeTable("select * from test");
            for (DataRow dataRow:dataTable.getRows()) {
                String nameStr = dataRow.getString("name");
                stringBuilder.append(nameStr).append(":");
            }
            LOGGER.info("print:{}", stringBuilder.toString());
        } catch (SQLException e) {
            LOGGER.error("", e);
        }
        return "select:" + stringBuilder.toString();

    }


}