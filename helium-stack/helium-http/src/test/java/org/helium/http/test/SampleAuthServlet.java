package org.helium.http.test;

import org.helium.util.Outer;
import org.helium.util.StringUtils;
import org.helium.framework.annotations.ServletImplementation;
import org.helium.http.servlet.HttpMappings;
import org.helium.http.servlet.HttpServlet;
import org.helium.http.servlet.HttpServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;

//import org.helium.framework.annotations.FixedExecutor;

/**
 * Created by Coral on 10/29/15.
 */
@ServletImplementation(id = "sample:AuthServlet")
//@FixedExecutor(name = "test", size = 10, limit = 50)
@HttpMappings(contextPath = "/sample", urlPattern = "/auth")
public class SampleAuthServlet extends HttpServlet {
	private static final Logger LOGGER = LoggerFactory.getLogger(SampleAuthServlet.class);

	private String getFromBASE64(String s) {
		if (s == null) {
			return null;
		}
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(s);
			return new String(b);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void process(HttpServletContext ctx) throws Exception {
//		ctx.getResponse().getOutputStream().write("Hello World".getBytes());
//		ctx.getResponse().getOutputStream().flush();

		String auth = ctx.getRequest().getHeader("Authorization");
		LOGGER.info("AuthHeader: {}", auth);

		if ((auth != null) && (auth.length() > 6)) {
			auth = auth.substring(6, auth.length());
			String decodedAuth = getFromBASE64(auth);
			Outer<String> user = new Outer<>();
			Outer<String> passwd = new Outer<>();
			if (!StringUtils.splitWithFirst(decodedAuth, ":", user, passwd)) {
				send401(ctx);
				return;
			}

			if ("root".equals(user.value()) && "passwd".equals(passwd.value())) {
				ctx.getResponse().getOutputStream().println("Hello World");
				ctx.getResponse().getOutputStream().flush();
			} else {
				send401(ctx);
				return;
			}
		} else {
			send401(ctx);
		}
	}
	private void send401(HttpServletContext ctx) {
		ctx.getResponse().setStatus(401);
		ctx.getResponse().setHeader("Cache-Control", "no-store");
		ctx.getResponse().setDateHeader("Expires", 0);
		ctx.getResponse().setHeader("WWW-authenticate", "Basic Realm=\"test\"");
	}
}
