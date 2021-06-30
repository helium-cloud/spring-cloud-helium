package org.helium.perfmon.controller;

import org.helium.perfmon.monitor.PullManager;
import org.helium.perfmon.observation.ObserverReport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by Coral on 2015/8/17.
 */
@RequestMapping("/perfmon/pull")
public class PullController {

	@GetMapping
	public void process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String key = request.getParameter("cookie");
        PullManager pullManager = PullManager.getInstance(key, false);
        if (pullManager == null) {
			response.sendError(404, String.format("Can't found subscribe info by '%s'", key));
            return;
        }
        List<ObserverReport> reports = pullManager.pull();
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (ObserverReport report : reports) {
            if (first) {
                first = false;
                sb.append('[');
            } else {
                sb.append(',');
            }
            sb.append(report.encodeToJson());
        }
        sb.append(']');
		response.setHeader("Content-Type", "application/json");
        ServletOutputStream out = response.getOutputStream();
        out.write(sb.toString().getBytes());
        out.close();
    }
}
