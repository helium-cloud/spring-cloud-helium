package org.helium.cloud.regsitercenter.configruation.registry;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.registry.Registry;
import org.apache.dubbo.registry.support.AbstractRegistryFactory;

/**
 * 类描述：直连注册工厂
 *
 * @author zkailiang
 * @date 2020/4/22
 */
public class DirectRegistryFactory extends AbstractRegistryFactory {
	@Override
	protected Registry createRegistry(URL url) {
		return new DirectRegistry();
	}
}
