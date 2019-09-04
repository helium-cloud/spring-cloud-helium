package org.helium.framework.route;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Coral on 8/10/15.
 * 1. 这个类的实现没考虑到线程安全的问题，
 * 2. 没过滤权值为0的类
 */
@Deprecated
public class ServerRouterGroup {
	private static final Random RAND = new Random();
	private int totalWeight;
	private List<ServerRouter> routers;

	public ServerRouterGroup() {
		totalWeight = 0;
		routers = new ArrayList<>();
	}

	public void addRouter(ServerRouter router) {
		routers.add(router);
		totalWeight += router.getWeight();
	}

	public BeanEndpoint routeBean() {
		if (totalWeight == 0) {
			return null;
		}

		int n = 0;
		int rand = RAND.nextInt(totalWeight);
		for (ServerRouter r: routers) {
			n += r.getWeight();
			if (rand < n) {
				return new BeanEndpoint(r.getBeanContext(), r.pickServer());
			}
		}
		throw new RuntimeException("ServerRouterGroup.routeBean with a BUG!!");
	}
}
