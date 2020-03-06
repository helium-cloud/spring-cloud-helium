package org.helium.framework.spi;

import org.helium.framework.BeanContext;
import org.helium.framework.BeanContextModification;
import org.helium.framework.BeanContextModification.Action;
import org.helium.framework.BeanContextService;
import org.helium.framework.BeanIdentity;
import org.helium.framework.route.ServerRouter;
import org.helium.util.CollectionUtils;
import org.helium.util.Event;
import org.helium.util.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 实现了BeanContext的管理, 但并不关心数据
 * Created by Coral on 7/26/15.
 */
public abstract class AbstractBeanContextService implements BeanContextService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBeanContextService.class);

	private final Object lock = new Object();
	private Map<BeanIdentity, BeanContext> beans;
	private Event<BeanContextModification> syncEvent;

	protected AbstractBeanContextService() {
		beans = new HashMap<>();
		syncEvent = new Event<>(this);
	}

	@Override
	public BeanContext getBean(BeanIdentity id) {
		synchronized (lock) {
			return beans.get(id);
		}
	}

	@Override
	public List<BeanContext> getBeans() {
		synchronized (lock) {
			return CollectionUtils.cloneValues(beans);
		}
	}

	public <E extends BeanContext> List<E> getBeans(Function<BeanContext, E> filter) {
		synchronized (lock) {
			return CollectionUtils.cloneValues(beans, filter);
		}
	}

	@Override
	public void addBean(BeanContext bean) {
		addBean(bean, false);
	}

	@Override
	public BeanContext putBean(BeanContext bean) {
		return addBean(bean, true);
	}

	@Override
	public BeanContext removeBean(BeanIdentity id) {
		BeanContext removed;
		synchronized (lock) {
			removed = beans.remove(id);
		}
		if (removed != null) {
			LOGGER.info("removeBean id={}", id);
			syncEvent.fireEvent(new BeanContextModification(Action.DELETE, removed));
		} else {
			LOGGER.info("removeBean id={} notFound", id);
		}
		return removed;
	}

	@Override
	public void syncBeans(EventHandler<BeanContextModification> handler, Function<BeanContext, Boolean> filter) {
		List<BeanContextModification> list = new ArrayList<>();
		synchronized (lock) {
			for (BeanContext bean: beans.values()) {
				if (filter.apply(bean)) {
					list.add(new BeanContextModification(Action.INSERT, bean));
				}
			}
			for (BeanContextModification m: list) {  // 为了数据一致性, 还是放在锁里面吧
				if (filter.apply(m.getBeanContext())) {
					handler.run(this, m);
				}
			}
			syncEvent.addListener((sender, m) -> {
				if (filter.apply(m.getBeanContext())) {
					handler.run(sender, m);
				}
			});
		}
	}

	@Override
	public void removeSyncHandler(EventHandler<BeanContextModification> handler) {
		syncEvent.removeListener(handler);
	}

	@Override
	public BeanContext findBeanContext(Object bean) {
		synchronized (lock) {
			for (BeanContext bc: beans.values()) {
				if (bean == bc.getBean()) {
					return bc;
				}
			}
		}
		return null;
	}

	protected BeanContext addBean(BeanContext bean, boolean replaceDuplicated) {
		BeanContext replaced;
		synchronized (lock) {
			if (beans.get(bean.getId()) != null && !replaceDuplicated) {
				throw new IllegalArgumentException("Duplicated beans id=" + bean.getId());
			}
			replaced = beans.put(bean.getId(), bean);
		}
		if (replaced != null) {
			LOGGER.info("putBean id={}", bean.getId());
			syncEvent.fireEvent(new BeanContextModification(Action.DELETE, replaced));
		} else {
			LOGGER.info("addBean id={}", bean.getId());
		}
		syncEvent.fireEvent(new BeanContextModification(Action.INSERT, bean));
		return replaced;
	}


	@Override
	public void notify(BeanContext ctx, Action type) {
		syncEvent.fireEvent(new BeanContextModification(type, ctx));
	}

	protected void beanContextChanged(BeanContext ctx) {
		syncEvent.fireEvent(new BeanContextModification(Action.UPDATE, ctx));
	}

	@Override
	public ServerRouter subscribeServerRouter(BeanContext bc, String bundleName, String protocol) {
		if (getCentralizedService() != null){
			return getCentralizedService().subscribeServerRouter(bc, bundleName, protocol);
		}
		return null;
	}

}
