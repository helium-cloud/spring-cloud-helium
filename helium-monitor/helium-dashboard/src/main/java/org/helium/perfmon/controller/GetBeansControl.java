package org.helium.perfmon.controller;

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
@RestController
public class GetBeansControl {


	@GetMapping
	@ResponseBody
	public List<BeanJson> getBeans(){
		List<BeanJson> list = new ArrayList<>();
		for (String bc: SpringContextUtil.getApplicationContext().getBeanDefinitionNames()) {
			BeanJson bean = new BeanJson();
			bean.setId(bc);
			bean.setType(bc);

			list.add(bean);
		}

		list.sort((l, r) -> {
			boolean lLocal = l.getBundle().endsWith("LOCAL");
			boolean rLocal = r.getBundle().endsWith("LOCAL");

			if (lLocal & !rLocal) {
				return -1;
			}
			if (!lLocal & rLocal) {
				return 1;
			}
			int r1 = l.getBundle().compareTo(r.getBundle());
			return r1 != 0 ? r1 : l.getId().compareTo(r.getId());
		});
		return list;
	}

}
