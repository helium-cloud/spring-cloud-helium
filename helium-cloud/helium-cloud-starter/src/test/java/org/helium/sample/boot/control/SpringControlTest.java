package org.helium.sample.boot.control;


import org.helium.framework.annotations.ServiceSetter;
import org.helium.framework.annotations.TaskEvent;
import org.helium.framework.task.TaskProducer;
import org.helium.sample.boot.config.CommonConfig;
import org.helium.sample.boot.service.HeliumServiceTest;
import org.helium.sample.boot.task.HeliumBootTaskTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringControlTest {

	@Autowired
	private CommonConfig config;

    @TaskEvent(HeliumBootTaskTest.TAG)
    private TaskProducer<String> workTaskTestTask;

	@ServiceSetter(id = "test:HeliumServiceTest")
	private HeliumServiceTest heliumServiceTest;

	@ServiceSetter
	private HeliumServiceTest heliumServiceTest1;

    @RequestMapping(value={"/hello"})
    public String say(){
		System.out.println(config);
		heliumServiceTest.test();
		heliumServiceTest1.test();
        return "index";
    }
}