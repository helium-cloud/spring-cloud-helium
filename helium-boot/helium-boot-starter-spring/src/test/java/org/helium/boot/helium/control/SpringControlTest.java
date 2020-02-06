package org.helium.boot.helium.control;


import org.helium.boot.helium.config.CommonConfig;
import org.helium.boot.helium.service.HeliumServiceTest;
import org.helium.boot.helium.task.HeliumBootTaskTest;
import org.helium.framework.annotations.ServiceSetter;
import org.helium.framework.annotations.TaskEvent;
import org.helium.framework.task.TaskProducer;
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