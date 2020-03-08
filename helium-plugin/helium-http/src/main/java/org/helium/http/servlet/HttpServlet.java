package org.helium.http.servlet;

import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.perfmon.Stopwatch;
import org.helium.http.servlet.spi.HttpServletCounters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Lei Gao on 7/6/15.
 */
public abstract class HttpServlet extends javax.servlet.http.HttpServlet {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpServlet.class);

	private HttpServletMappings mappings = null;

	private HttpServletCounters counter;

	private String servletName;

	public HttpServlet() {

	}


	protected void setMappings(HttpServletMappings mappings) {
		this.mappings = mappings;
	}

	public HttpServletMappings getMappings() {
		return this.mappings;
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) {
//		Stopwatch w2 = null;
//		if (counter != null) {
//			counter.getRequest().increase();
//			counter.getThroughput().increaseBy(req.getContentLength());
//			w2 = counter.getTx().begin();
//		}
		try {
			HttpServletContext ctx = new HttpServletContext(req, resp);
			process(ctx);
//			if (w2 != null) {
//				w2.end();
//			}
		} catch (Exception ex) {
			LOGGER.error("", ex);
			if (!resp.isCommitted()) {
				sendErrorMessage(resp, ex);
			}
//			if (w2 != null) {
//				w2.fail(ex);
//			}
		}
	}

	private void sendErrorMessage(HttpServletResponse resp, Exception ex) {
		try {
			resp.sendError(500, ex.toString());
		} catch (IOException e) {
			LOGGER.error("sendErrorMessage Failed:", e);
		}
	}

	public abstract void process(HttpServletContext ctx) throws Exception;

	@Override
	public String getServletName() {
		return servletName;
	}

	public void setServletName(String servletName) {
		this.servletName = servletName;
		this.counter = PerformanceCounterFactory.getCounters(HttpServletCounters.class, servletName);
	}

	public HttpServletCounters getCounter(){
		return counter;
	}
}
