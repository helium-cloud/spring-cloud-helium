//package org.helium.sample.old.quickstart;
//
//import org.helium.framework.annotations.FieldSetter;
//import org.helium.framework.annotations.ServiceSetter;
//import org.helium.framework.annotations.ServletImplementation;
//import org.helium.framework.annotations.TaskEvent;
//import org.helium.framework.task.TaskProducer;
//import org.helium.http.servlet.HttpMappings;
//import org.helium.logging.spi.LogUtils;
//import org.helium.redis.RedisClient;
//
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * HttpServlet例子
// * Created by Coral on 5/11/15.
// */
//@ServletImplementation(id = "quickstart:SampleServlet3")
//@HttpMappings(contextPath = "/quickstart", urlPattern = "/sample3")
//public class SampleHttpServlet3 extends HttpServlet {
//	/**
//	 * SampleService
//	 */
//	@ServiceSetter
//	private SampleService sampleService;
//
//	@FieldSetter("SAMPLERD")
//	private RedisClient redis;
//
//	@TaskEvent(SampleLogTask.EVENT_NAME)
//	private TaskProducer<SampleLogTaskArgs> logTask;
//
//	@Override
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		try {
//			int id = Integer.parseInt(req.getParameter("id"));
//			String key = "QUICKSTART-" + id;
//
//			//
//			// 尝试从Redis中读取缓存，如果redis中不存在，则读取数据库
//			SampleUser user = redis.get(key, SampleUser.class);
//			if (user == null) {
//				user = sampleService.getUser(id);
//				redis.set(key, user);
//			}
//
//			resp.getOutputStream().print(user.toJsonObject().toString());
//
//			//
//			// 通过logTask生产SampleLogTask
//			SampleLogTaskArgs logArgs = new SampleLogTaskArgs();
//			logArgs.setClientIp(req.getRemoteAddr());
//			logArgs.setAction("GET");
//			logArgs.setUser(user);
//			logTask.produce(logArgs);
//		} catch (Exception e) {
//			resp.getOutputStream().println(LogUtils.formatError(e));
//		}
//		resp.getOutputStream().close();
//	}
//}
