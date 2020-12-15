package org.helium.framework.servlet;

import org.helium.framework.BeanContext;
import org.helium.framework.route.ServerUrl;

import java.util.List;

/**
 * 实现ServletStack
 * Created by Coral on 4/23/15.
 */
public interface ServletStack {

	/**
	 * 获取StackId
	 *
	 * @return
	 */
	String getId();

	/**
	 * 设置StackId
	 */
	void setId(String id);

	/**
	 * 获取ServletStack监听的端口
	 * @return
	 */
	List<ServerUrl> getServerUrls();

	/**
	 * 是否支持此Servlet
	 * @param servlet
	 * @return
	 */
	boolean isSupportServlet(Object servlet);

	/**
	 * 是否支持此Module
	 * @param module
	 * @return
	 */
	boolean isSupportModule(Object module);

	/**
	 * 返回Servlet的描述器
	 * @return
	 */
	ServletDescriptor getServletDescriptor();

	/**
	 * 注册一个Module
	 * @param context
	 */
	void registerModule(BeanContext context);

	/**
	 *
	 * 注册一个servlet, 由容器负责分配并调用, 注册成功后返回一个ServletWrapper,
	 * ServletWrapper提供给BeanContext层面实现Module的调用
	 */
	void registerServlet(BeanContext context);

	/**
	 * 移除一个Module
	 * @param context
	 */
	void unregisterModule(BeanContext context);

	/**
	 * 移除一个Servlet
	 * @param context
	 */
	void unregisterServlet(BeanContext context);

	/**
	 * 启动Stack
	 */
	void start() throws Exception;

	/**
	 * 停止Stack
	 */
	void stop() throws Exception;

	default String getHost(){
		return "127.0.0.1";
	}
}
