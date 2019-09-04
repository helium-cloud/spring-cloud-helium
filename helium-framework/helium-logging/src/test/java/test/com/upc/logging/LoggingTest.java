package test.com.upc.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LoggingTest {

	private static final Logger log4jLogger = LoggerFactory.getLogger(LoggingTest.class);
	private static final java.util.logging.Logger jdkLogger = java.util.logging.Logger.getLogger(LoggingTest.class.getName());
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LoggingTest.class);
	private static final org.slf4j.Logger logger2 = LoggerFactory.getLogger("org.test.logger.Tester");

	/**
	 * @param args
	 */
	public static void main(String[] args) throws InterruptedException, IOException {
		System.out.println(System.getProperty("user.dir"));
		Path path = Paths.get(System.getProperty("user.dir") + "/logging.xml");
		InputStream is = LoggingTest.class.getClassLoader().getResourceAsStream("logging.xml");
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = is.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}

		System.out.println(path.toString());
		System.out.println(out.toString());

		log4jLogger.info("Slf4j日志");
		jdkLogger.info("JDK Log日志");

		logger.debug("motherfucker 1", new RuntimeException());
		Thread.sleep(6000);

		logger.trace("motherfucker trace");
		logger.debug("motherfucker debug");
		logger.info("motherfucker info");
		logger.warn("motherfucker warn");
		logger.error("motherfucker error", new RuntimeException("aaa"));

		logger2.trace("motherfucker trace");
		logger2.debug("motherfucker debug");
		logger2.info("motherfucker info");
		logger2.warn("motherfucker warn");
		logger2.error("motherfucker error");

		Marker m = MarkerFactory.getMarker("foo");
		logger2.trace(m, "motherfucker trace");
		logger2.debug(m, "motherfucker debug");
		logger2.info(m, "motherfucker info");
		logger2.warn(m, "motherfucker warn");
		logger2.error(m, "motherfucker error");

		Thread.sleep(5000);


//
////		ConfigProviderImpl configProvider = new ConfigProviderImpl();
////		configProvider.addPath("helium-logging/build/resources/test");
////		LoggingConfiguration configuration = configProvider.loadXml("logging.xml", LoggingConfiguration.class);
////		new LoggerManagerImpl().applySettings(configuration);
//
//		logger.debug("motherfucker 1", new RuntimeException());
//		logger.trace("motherfucker 2", new RuntimeException());
//		logger.info("motherfucker 3", new RuntimeException());
//		logger.warn("motherfucker 4", new RuntimeException());
//		logger.error("motherfucker 5", new RuntimeException());
//
//		while (true) {
//			Thread.sleep(1000);
//			logger.error("sdfsdfasdf");
//		}
	}
}
