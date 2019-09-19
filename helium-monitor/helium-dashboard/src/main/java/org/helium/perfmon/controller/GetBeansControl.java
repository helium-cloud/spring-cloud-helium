package org.helium.perfmon.controller;

import com.alibaba.fastjson.JSONObject;
import org.helium.cloud.common.utils.SpringContextUtil;
import org.helium.framework.annotations.ServletImplementation;
import org.helium.framework.entitys.dashboard.BeanJson;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coral on 7/28/15.
 */
@RequestMapping("/dashboard/bean")

public class GetBeansControl {


	@GetMapping
	@ResponseBody
	public List<JSONObject> getBeans(){
		List<JSONObject> list = new ArrayList<>();
		for (String bn: SpringContextUtil.getApplicationContext().getBeanDefinitionNames()) {
			JSONObject bean = new JSONObject();
			bean.put("name", bn);
			list.add(bean);
		}
		return list;
	}

}
