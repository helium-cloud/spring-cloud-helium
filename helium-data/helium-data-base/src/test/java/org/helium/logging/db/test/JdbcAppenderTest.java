package org.helium.logging.db.test;//package org.helium.logging.db.test;
//
//import org.helium.logging.db.JdbcAppender;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * Created by Coral on 11/2/15.
// */
//public class JdbcAppenderTest {
//	private static final Logger LOGGER = LoggerFactory.getLogger(JdbcAppenderTest.class);
//
//	public static void main(String[] args) {
//		JdbcAppender appender = new JdbcAppender("jdbc:mysql://10.10.220.107:9306/LogDB", "admin", "admin");
//		appender.open();
//
//		while (true) {
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			LOGGER.warn("Tick-Tock");
//		}
//	}
//}
