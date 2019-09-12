package org.helium.sample.boot.control;


import org.helium.cloud.task.annotations.TaskEvent;
import org.helium.cloud.task.api.TaskProducer;
import org.helium.sample.boot.task.HeliumBootTaskTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringControlTest {


    @TaskEvent(HeliumBootTaskTest.TAG)
    private TaskProducer<String> workTaskTestTask;


    @RequestMapping(value={"/hello"})
    public String say(){
		//System.out.println(config.getUploadPath());
		workTaskTestTask.produce("222");
        return "index";
    }
}