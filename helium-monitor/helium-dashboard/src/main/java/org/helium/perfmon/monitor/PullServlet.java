package org.helium.perfmon.monitor;

import org.helium.perfmon.observation.ObserverReport;
import org.helium.framework.annotations.ServletImplementation;
import org.helium.http.servlet.HttpMappings;
import org.helium.http.servlet.HttpServlet;
import org.helium.http.servlet.HttpServletContext;

import javax.servlet.ServletOutputStream;
import java.util.List;

/**
 * Created by Coral on 2015/8/17.
 */
@ServletImplementation(id = "perfmon:PullServlet")
@HttpMappings(contextPath = "/perfmon", urlPattern = "/pull")
public class PullServlet extends HttpServlet {

    @Override
    public void process(HttpServletContext ctx) throws Exception {
        String key = ctx.getRequest().getParameter("cookie");
        PullManager pullManager = PullManager.getInstance(key, false);
        if (pullManager == null) {
            ctx.getResponse().sendError(404, String.format("Can't found subscribe info by '%s'", key));
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
        ctx.getResponse().setHeader("Content-Type", "application/json");
        ServletOutputStream out = ctx.getResponse().getOutputStream();
        out.write(sb.toString().getBytes());
        out.close();
    }
}
