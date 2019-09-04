//package org.helium.sample.old.advanced;
//
//import org.helium.database.Database;
//
//import org.helium.framework.annotations.FieldSetter;
//import org.helium.framework.annotations.ServiceSetter;
//import org.helium.framework.annotations.ServletImplementation;
//import org.helium.framework.configuration.legacy.ConfigParams;
//import org.helium.framework.configuration.legacy.ConfigTable;
//import org.helium.framework.configuration.legacy.HAConfigService;
//import org.helium.framework.tag.Initializer;
//import org.helium.http.servlet.HttpMappings;
//import org.helium.http.servlet.HttpServlet;
//import org.helium.http.servlet.HttpServletContext;
//import org.helium.redis.cluster.RedisCluster;
//import org.helium.redis.cluster.RedisSentinelCluster;
//
///**
// * Created by Coral on 7/13/17.
// */
//@ServletImplementation(id = "quickstart:HAConfigSampleServlet")
//@HttpMappings(contextPath = "/quickstart", urlPattern = "/ha")
//public class HAConfigSampleServlet extends HttpServlet {
//	@ServiceSetter
//	private HAConfigService configService;
//
//	private String configText;
//
////	private ConfigTable<Integer, CFG_LogicalPool> configTable;
////
////
////	@Initializer
////	public void initialize() throws Exception {
////		configText = configService.loadConfigText("addressbook.properties", new ConfigParams());
////
////		configTable = configService.loadConfigTable(Integer.class, CFG_LogicalPool.class, "CFG_LogicalPool");
////
////	}
//
////	@FieldSetter("IICUPDB")             // 从FAE_Resource表读取
////	private HAPooledDatabase updb;
//
//	@FieldSetter("PRS-Online")         // 从FAE_RedisCluster表读取
//	private RedisCluster prsOnline;
//
//	@FieldSetter("SIMS-Contents")      // 从FAE_RedisSentinels表读取
//	private RedisSentinelCluster simsContents;
//
//	@Override
//	public void process(HttpServletContext ctx) throws Exception {
////		try {
//			ctx.getResponse().getOutputStream().println("addressbook.properties:" + configText + " <br>");
////			configTable.getHashtable().forEach((k, v) -> {
////				try {
////					ctx.getResponse().getOutputStream().println("logicalPool:  " + v.getLogicalPoolId() + "<br>");
////				} catch (Exception ex) {
////					throw new RuntimeException(ex);
////				}
////			});
////		} catch (Exception ex) {
////			ctx.sendError(ex);
////		}
//	}
//
//	private void foo() {
////		Database db = updb.getSharding(new LogicalPooledObject() {
////			@Override
////			public int getLogicalPool() {
////				return 1;
////			}
////		});
//
//
//
//
//	}
//}
//
//
