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
@RequestMapping("/perfmon/subscribe")
@RestController
public class SubscribeController {
	@RequestMapping
	public void process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String method = request.getMethod();

        switch (method) {
            case "POST":
                subscribe(request, response);
                break;
            case "DELETE":
                unsubscribe(request, response);
                break;
            default:
                response.sendError(405, String.format("Not support method '%s', please ues 'POST' or 'DELETE'", method));
        }
    }

    public void subscribe(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String category = request.getParameter("category");
        String option = request.getParameter("instance");
        String intervalStr = request.getParameter("interval");
        int interval = (intervalStr == null || intervalStr.isEmpty()) ? 1 : Integer.parseInt(intervalStr);
        String key = request.getParameter("cookie");

        Observable ob = ObserverManager.getObserverItem(category);
        ObserverReportMode mode = ObserverReportMode.valueOf(option.toUpperCase());
        if (ob == null) {
			response.sendError(404, String.format("Can't found target category '%s'", category));
            return;
        }

        final PullManager manager = PullManager.getInstance(key, true);
        TimeSpan span = new TimeSpan(interval * TimeSpan.SECOND_MILLIS);
        ObserverManager.addInspector(ob, mode, span, report -> {
            if (!manager.isActive()) {
                return false;
            } else {
                manager.enqueueReport(report);
                return true;
            }
        });
    }

    public void unsubscribe(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String key = request.getParameter("cookie");
        PullManager pullManager = PullManager.getInstance(key, false);
        if (pullManager != null) {
            pullManager.close();
        }
    }
}
