//package org.helium.sample.old.advanced;
//
//import org.helium.rpc.channel.RpcFuture;
//import org.helium.rpc.client.RpcMethodStub;
//import com.feinno.superpojo.SuperPojo;
//import com.feinno.superpojo.annotation.Field;
//import org.helium.framework.annotations.FieldSetter;
//import org.helium.framework.annotations.ServletImplementation;
//import org.helium.framework.rpc.LegacyRpcClient;
//import org.helium.http.servlet.HttpMappings;
//import org.helium.http.servlet.HttpServlet;
//import org.helium.http.servlet.HttpServletContext;
//
///**
// * Created by Coral on 7/13/17.
// */
//@ServletImplementation(id = "quickstart:LegacyRpcSampleServlet")
//@HttpMappings(contextPath = "/quickstart", urlPattern = "/rpc")
//public class LegacyRpcSampleServlet extends HttpServlet {
//	@FieldSetter("rpc/fetion-cats.xml")
//	private LegacyRpcClient catsRpcClient; // 路径上的rpc在2.3.1版本中是可以省略掉的, 2.1.9以前的版本是历史问题必须保留
//
//	@Override
//	public void process(HttpServletContext ctx) throws Exception {
//		RpcMethodStub methodStub = catsRpcClient.getMethodStub("GetUserIndexByMobileNo");
//		FxMobileNo mobileNo = FxMobileNo.parse(ctx.getRequest().getParameter("mobile"));
//		RpcFuture future = methodStub.invoke(mobileNo);
//		FxCatalogIndex fxCatalogIndex = future.syncGet(FxCatalogIndex.class);
//		if (fxCatalogIndex != null) {
//			ctx.getResponse().getOutputStream().println(fxCatalogIndex.toJsonObject().toString());
//		} else {
//			ctx.getResponse().getOutputStream().println("USER NOT FOUND:" + mobileNo.toString());
//		}
//	}
//
//	public static class FxCatalogIndex extends SuperPojo {
//		@Field(id = 1)
//		private int userId;
//
//		@Field(id = 2)
//		private int logicalPoolId;
//
//		@Field(id = 3)
//		private boolean isDirty;
//
//		@Field(id = 4)
//		private int sid;
//
//		@Field(id = 5)
//		private long mobileNo;
//
//		/**
//		 * @return the userId
//		 */
//		public int getUserId() {
//			return userId;
//		}
//
//		/**
//		 * @param userId the userId to set
//		 */
//		public void setUserId(int userId) {
//			this.userId = userId;
//		}
//
//		/**
//		 * @return the logicalPoolId
//		 */
//		public int getLogicalPoolId() {
//			return logicalPoolId;
//		}
//
//		/**
//		 * @param logicalPoolId the logicalPoolId to set
//		 */
//		public void setLogicalPoolId(int logicalPoolId) {
//			this.logicalPoolId = logicalPoolId;
//		}
//
//		/**
//		 * @return the isDirty
//		 */
//		public boolean getIsDirty() {
//			return isDirty;
//		}
//
//		/**
//		 * @param isDirty the isDirty to set
//		 */
//		public void setIsDirty(boolean isDirty) {
//			this.isDirty = isDirty;
//		}
//
//		/**
//		 * @return the sid
//		 */
//		public int getSid() {
//			return sid;
//		}
//
//		/**
//		 * @param sid the sid to set
//		 */
//		public void setSid(int sid) {
//			this.sid = sid;
//		}
//
//		public long getMobileNo() {
//			return mobileNo;
//		}
//
//		public void setMobileNo(long mobileNo) {
//			this.mobileNo = mobileNo;
//		}
//	}
//}
