package org.helium.framework.route.center;

import java.util.Map;

/**
 * Created by Coral on 8/5/15.
 */
@FunctionalInterface
public interface CentralizedMonitor {
	void update(Map<String, byte[]> datas);
}
