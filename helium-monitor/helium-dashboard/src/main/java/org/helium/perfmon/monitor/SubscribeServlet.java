package org.helium.perfmon.monitor;


import com.feinno.superpojo.type.TimeSpan;
import org.helium.framework.annotations.ServletImplementation;
import org.helium.http.servlet.HttpMappings;
import org.helium.http.servlet.HttpServlet;
import org.helium.http.servlet.HttpServletContext;
import org.helium.perfmon.observation.Observable;
import org.helium.perfmon.observation.ObserverManager;
import org.helium.perfmon.observation.ObserverReportMode;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by Coral on 2015/8/17.
 */
@ServletImplementation(id = "perfmon:SubscribeServlet")
@HttpMappings(contextPath = "/perfmon", urlPattern = "/subscribe")
public class SubscribeServlet extends HttpServlet {
    @Override
    public void process(HttpServletContext ctx) throws Exception {
        String method = ctx.getRequest().getMethod();

        switch (method) {
            case "POST":
                subscribe(ctx);
                break;
            case "DELETE":
                unsubscribe(ctx);
                break;
            default:
                ctx.getResponse().sendError(405, String.format("Not support method '%s', please ues 'POST' or 'DELETE'", method));
        }
    }

    public void subscribe(HttpServletContext ctx) throws IOException {
        HttpServletRequest request = ctx.getRequest();

        String category = request.getParameter("category");
        String option = request.getParameter("instance");
        String intervalStr = request.getParameter("interval");
        int interval = (intervalStr == null || intervalStr.isEmpty()) ? 1 : Integer.parseInt(intervalStr);
        String key = request.getParameter("cookie");

        Observable ob = ObserverManager.getObserverItem(category);
        ObserverReportMode mode = ObserverReportMode.valueOf(option.toUpperCase());
        if (ob == null) {
            ctx.getResponse().sendError(404, String.format("Can't found target category '%s'", category));
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

    public void unsubscribe(HttpServletContext ctx) throws IOException {
        String key = ctx.getRequest().getParameter("cookie");
        PullManager pullManager = PullManager.getInstance(key, false);
        if (pullManager != null) {
            pullManager.close();
        }
    }
}
