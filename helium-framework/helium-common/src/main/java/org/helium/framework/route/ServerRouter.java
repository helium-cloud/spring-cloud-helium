package org.helium.framework.route;

import org.helium.framework.BeanContext;

import java.util.List;

/**
 * 描述一个ServletEndpoint所在的地址
 * Created by Coral on 8/8/15.
 */
public interface ServerRouter {
	int DEFAULT_WEIGHT = 100;

	/**
	 * 权值, 决定负载的量
	 * @return
	 */
	int getWeight();

	/**
	 * 返回一个BeanContext
	 * @return
	 */
	BeanContext getBeanContext();

	/**
	 * 得到具体服务器地址
	 * @return
	 */
	ServerUrl pickServer();

	/**
	 * 使用Hash方式获得具体服务器地址
	 * @param tag
	 * @return
	 */
	ServerUrl pickServer(String tag);

	/**
	 *
	 * @return
	 */
	default BeanEndpoint routeBean() {
		ServerUrl url = pickServer();
		if (url == null) {
			return null;
		}
		return new BeanEndpoint(getBeanContext(), url);
	}

	/**
	 * router中是否存在此url
	 * @param url
	 * @return
	 */
	boolean hasServer(ServerUrl url);

	/**
	 * 获取全部的Url
	 * @return
	 */
	List<ServerUrl> getAllUrls();
}
