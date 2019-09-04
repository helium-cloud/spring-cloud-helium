package org.helium.framework.route;

import org.helium.util.LoopCounter;
import org.helium.framework.BeanContext;
import org.helium.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Coral on 8/8/15.
 */
public class StaticServerRouter implements ServerRouter {
	private BeanContext beanContext;
	private LoopCounter lc;
	private ServerUrl[] urls = new ServerUrl[0];
	private KetamaHashLocator<ServerUrl> hashLocator;

	@Override
	public int getWeight() {
		if (urls == null) {
			return 0;
		} else {
			return urls.length * ServerRouter.DEFAULT_WEIGHT;
		}
	}

	@Override
	public BeanContext getBeanContext() {
		return beanContext;
	}


	@Override
	public ServerUrl pickServer() {
		if (urls == null || urls.length == 0) {
			return null;
		} else {
			return urls[lc.next()];
		}
	}

	@Override
	public ServerUrl pickServer(String tag) {
		return hashLocator.getPrimary(tag);
	}

	@Override
	public boolean hasServer(ServerUrl a) {
		for (ServerUrl url: urls) {
			if (url.equals(a)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<ServerUrl> getAllUrls() {
		return Arrays.asList(urls);
	}

	public void addServer(ServerUrl url) {
		if (url == null) {
			throw new IllegalArgumentException("url == null");
		}
		synchronized (this) {
			urls = CollectionUtils.appendArray(urls, url);
			hashLocator = new KetamaHashLocator<ServerUrl>(urls);
			lc = new LoopCounter(urls.length);
		}
	}

	public boolean removeServer(ServerUrl url) {
		ServerUrl[] old = urls;
		synchronized (this) {
			urls = CollectionUtils.removeArrayIf(urls, u -> u.equals(url));
			hashLocator = new KetamaHashLocator<ServerUrl>(urls);
			lc = new LoopCounter(urls.length);
			return urls.length < old.length;
		}
	}
}
