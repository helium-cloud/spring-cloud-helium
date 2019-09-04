package org.helium.http.servlet.spi;

import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.util.HtmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Coral on 7/23/15.
 */
public class HttpUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

	public static String getRelativePath(String requestUrl, String context) {
		return requestUrl.substring(context.length());
	}

	public static void sendErrorPage(Response response, Throwable error) {
		try {
			HtmlHelper.setErrorAndSendErrorPage(response.getRequest(), response, response.getErrorPageGenerator(), 500, "Internal Error",
					"Internal Error", error);
		} catch (IOException e1) {
			LOGGER.error("sendErrorPage failed", e1);
		}
	}
}
