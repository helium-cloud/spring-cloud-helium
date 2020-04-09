package org.helium.servlet.servlet;

import org.springframework.beans.factory.annotation.Autowired;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 类描述：发送短信
 *
 * @author zhangkailiang@feinno.com
 * @date 2020/2/24
 */
@WebServlet(urlPatterns = "/ims/sms")
public class HttpImsSmsServlet extends HttpServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpImsSmsServlet.class);

	@Autowired
	private Im5GSmsService im5GSmsService;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		LOGGER.info("doRequest Post Start:{}:\r\n{}", request.getRequestURI());

		response.setStatus(200);
	}
}
