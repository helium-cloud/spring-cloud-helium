package com.coral.learning.dubbo.cunsmer.control;

import com.coral.learning.dubbo.api.MessageRequest;
import com.coral.learning.dubbo.api.MessageResponse;
import com.coral.learning.dubbo.api.MessageService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class DubboControl {

    //注册中心负载-dubbo
    @Reference(protocol = "dubbo", check = false)
    private MessageService messageServiceDubbo;

	//注册中心负载-helium
	@Reference(protocol = "helium")
	private MessageService messageServiceHelium;

    @GetMapping("/dubbo/test")
    @ResponseBody
    public MessageResponse doTestDubbo(){
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setTid(UUID.randomUUID().toString());
        System.out.println("call send for dubbo");
        MessageResponse messageResponse = messageServiceDubbo.send(messageRequest);
        return messageResponse;
    }


	@GetMapping("/helium/test")
	@ResponseBody
	public MessageResponse doTest(){
		MessageRequest messageRequest = new MessageRequest();
		messageRequest.setTid(UUID.randomUUID().toString());
		System.out.println("call send for helium");
		MessageResponse messageResponse = messageServiceHelium.send(messageRequest);
		return messageResponse;
	}
}
