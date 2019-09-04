package org.helium.framework.servlet;

import org.helium.framework.annotations.ServiceInterface;
import org.helium.framework.entitys.BootstrapConfiguration;
import org.helium.framework.route.ServerUrl;

import java.util.List;

/**
 * 用于管理ServletDescriptor的接口, 注册在根BeanContextService之上
 * @see org.helium.framework.BeanContextService
 * Created by Coral on 7/4/15.
 */
@ServiceInterface(id = StackManager.BEAN_ID)
public interface StackManager {
	String BEAN_ID = "helium:StackManager";

	/**
	 * @param id
	 * @return
	 */
	ServletStack getStack(String id);

	/**
	 * @return
	 */
	List<ServletStack> getStacks();

	/**
	 * 获取一个特定的Servlet描述器
	 * @param servlet
	 * @return
	 */
	ServletDescriptor getServletDescriptor(Object servlet);

	/**
	 * 获取一个特定的Servlet描述器
	 * @param module
	 * @return
	 */
	ServletDescriptor getModuleDescriptor(Object module);

	/**
	 * 通过协议获得一个Descriptor
	 * @return
	 */
	ServletDescriptor getDescriptor(String protocol);

	/**
	 * 获取本机启动的所有ServerUrl
	 * @return
	 */
	List<ServerUrl> getServerUrls();
	/**
	 * 获取本机启动的所有ServerUrl
	 * @return
	 */
	List<ServerUrl> getCenterServerUrls(BootstrapConfiguration configuration);
}
