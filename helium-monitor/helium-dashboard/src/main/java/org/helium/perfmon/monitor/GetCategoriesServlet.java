package org.helium.perfmon.monitor;

import org.helium.perfmon.observation.Observable;
import org.helium.perfmon.observation.ObserverManager;
import org.helium.framework.annotations.ServletImplementation;
import org.helium.http.servlet.HttpMappings;
import org.helium.http.servlet.HttpServlet;
import org.helium.http.servlet.HttpServletContext;

/**
 * Created by Coral on 2015/8/17.
 */
@ServletImplementation(id = "perfmon:GetCategoriesServlet")
@HttpMappings(contextPath = "/perfmon", urlPattern = "/categories")
public class GetCategoriesServlet extends HttpServlet {
    @Override
    public void process(HttpServletContext ctx) throws Exception {
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

        ctx.getResponse().setHeader("Content-Type", "application/json");
        ctx.getResponse().getOutputStream().write(sb.toString().getBytes());
        ctx.getResponse().getOutputStream().close();
    }
}
