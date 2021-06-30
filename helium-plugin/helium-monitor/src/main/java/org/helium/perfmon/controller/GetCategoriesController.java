package org.helium.perfmon.controller;

import org.helium.perfmon.monitor.EntityUtil;
import org.helium.perfmon.observation.Observable;
import org.helium.perfmon.observation.ObserverManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by Coral on 2015/8/17.
 */

@RequestMapping("/perfmon/categories")
public class GetCategoriesController {

	@GetMapping
    public void process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Observable observable : ObserverManager.getAllObserverItems()) {
            if (first) {
                sb.append('[');
                first = false;
            } else {
                sb.append(',');
            }
            sb.append(EntityUtil.convert(observable).toJsonObject().toString());
        }
        sb.append(']');

		response.setHeader("Content-Type", "application/json");
		response.getOutputStream().write(sb.toString().getBytes());
		response.getOutputStream().close();
    }
}
