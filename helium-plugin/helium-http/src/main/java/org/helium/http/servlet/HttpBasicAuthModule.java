package org.helium.http.servlet;

import org.helium.framework.module.ModuleState;
import org.helium.util.Outer;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

/**
 * Created by Lei Gao on 10/29/15.
 */
public abstract class HttpBasicAuthModule implements HttpModule {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpBasicAuthModule.class);
	private String contextPath;

	@Override
	public String getContextPath() {
		return contextPath;
	}

	protected abstract boolean doAuthentication(String user, String passwd);

	@Override
	public ModuleState processModule(HttpServletContext ctx) {
		// String user = (String)ctx.getRequest().getSession().getAttribute("user");

		String auth = ctx.getRequest().getHeader("Authorization");
		LOGGER.info("AuthHeader: {}", auth);

		if ((auth != null) && (auth.length() > 6)) {
			auth = auth.substring(6, auth.length());
			String decodedAuth = getFromBASE64(auth);
			Outer<String> user = new Outer<>();
			Outer<String> passwd = new Outer<>();
			if (StringUtils.splitWithFirst(decodedAuth, ":", user, passwd)) {
				if (doAuthentication(user.value(), passwd.value())) {
					// ctx.getRequest().getSession().setAttribute("user", user.value());
					return ModuleState.newCompleted();
				}
			}
		}

		return send401(ctx);
	}

	@Override
	public boolean isMatch(HttpServletContext context) {
		return true;
	}

	private String getFromBASE64(String s) {
		if (s == null) {
			return null;
		}
		try {
			byte[] b = Base64.getDecoder().decode(s);
			return new String(b);
		} catch (Exception e) {
			return null;
		}
	}

	private ModuleState send401(HttpServletContext ctx) {
		ctx.getResponse().setStatus(401);
		ctx.getResponse().setHeader("Cache-Control", "no-store");
		ctx.getResponse().setDateHeader("Expires", 0);
		ctx.getResponse().setHeader("WWW-authenticate", "Basic Realm=\"test\"");
		return ModuleState.newTerminated(null);
	}
}
