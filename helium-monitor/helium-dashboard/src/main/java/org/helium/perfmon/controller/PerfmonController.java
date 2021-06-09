package org.helium.perfmon.controller;


import com.feinno.superpojo.type.TimeSpan;
import org.helium.perfmon.monitor.PullManager;
import org.helium.perfmon.observation.Observable;
import org.helium.perfmon.observation.ObserverManager;
import org.helium.perfmon.observation.ObserverReportMode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Coral on 2015/8/17.
 */
@RequestMapping("/perfmon")
@RestController
public class PerfmonController {
	@GetMapping
	public String redirctView(HttpServletResponse response)  {
		try {
			response.sendRedirect("/perfmon/index.html");
		} catch (IOException e) {
			return e.getMessage();
		}
		return "redirct perfmon";
	}

}