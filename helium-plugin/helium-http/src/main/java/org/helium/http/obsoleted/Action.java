package org.helium.http.obsoleted;//package helium.http;
//
//import com.feinno.superpojo.SuperPojo;
//import com.feinno.superpojo.annotation.Entity;
//import com.feinno.superpojo.annotation.Field;
//import com.feinno.superpojo.annotation.NodeType;
//import com.feinno.superpojo.util.SuperPojoUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.List;
//
///**
// * Action配置
// *
// * @author Lv.Mingwei
// *
// */
//@Entity(name = "action")
//public class Action extends SuperPojo {
//
//	@Field(id = 1, name = "class", type = NodeType.ATTR)
//	private String clazz;
//
//	/** 结果列表 */
//	@Field(id = 2, name = "result")
//	private List<Result> results;
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(Action.class);
//
//	public HeliumHttpAction getClassObject(ClassLoader loader) {
//		try {
//			return (HeliumHttpAction) Class.forName(clazz, true, loader).newInstance();
//		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
//			LOGGER.error("Get Http Action failed.", e);
//		}
//		return null;
//	}
//
//	public String getClassName() {
//		return clazz;
//	}
//
//	public void setInterfaceClazz(String clazz) {
//		this.clazz = clazz;
//	}
//
//	public Result getResult(String name) {
//		if (name == null || results == null || results.size() == 0) {
//			return null;
//		}
//		for (Result result : results) {
//			if (name.equals(result.getName())) {
//				return result;
//			}
//		}
//		return null;
//	}
//
//	public List<Result> getResults() {
//		return results;
//	}
//
//	public void setResults(List<Result> results) {
//		this.results = results;
//	}
//
//	/**
//	 * Action 配置的子节点
//	 *
//	 * @author Lv.Mingwei
//	 *
//	 */
//	public static class Result extends SuperPojo {
//
//		@Field(id = 1, type = NodeType.ATTR)
//		private String name;
//
//		public final String getName() {
//			return name;
//		}
//
//		public final void setName(String name) {
//			this.name = name;
//		}
//
//		public String getFile() {
//			return SuperPojoUtils.getStringAnyNode(this);
//		}
//
//	}
//}
