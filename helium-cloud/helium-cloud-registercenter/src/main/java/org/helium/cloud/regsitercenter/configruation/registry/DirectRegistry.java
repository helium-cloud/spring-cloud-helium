package org.helium.cloud.regsitercenter.configruation.registry;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.registry.NotifyListener;
import org.apache.dubbo.registry.Registry;

import java.util.List;

/**
 * 类描述：直连注册
 *
 * @author zkailiang
 * @date 2020/4/22
 */
public class DirectRegistry implements Registry {

	private URL url;

	@Override
	public URL getUrl() {
		return this.url;
	}

	@Override
	public boolean isAvailable() {
		return false;
	}

	@Override
	public void destroy() {

	}

	@Override
	public void register(URL url) {
		this.url = url;
	}

	@Override
	public void unregister(URL url) {

	}

	@Override
	public void subscribe(URL url, NotifyListener listener) {

	}

	@Override
	public void unsubscribe(URL url, NotifyListener listener) {

	}

	@Override
	public List<URL> lookup(URL url) {
		return null;
	}
}
