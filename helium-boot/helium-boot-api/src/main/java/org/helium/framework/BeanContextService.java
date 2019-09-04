package org.helium.framework;

import org.helium.util.EventHandler;
import org.helium.framework.annotations.ServiceInterface;
import org.helium.framework.bundle.BundleManager;
import org.helium.framework.route.center.CentralizedService;
import org.helium.framework.route.ServerRouter;

import java.util.List;
import java.util.function.Function;

/**
 * 提供Bean获取功能给BeanContext作为组装
 * Created by Coral on 7/4/15.
 */
public interface BeanContextService {
	/**
	 * 使用Id获取一个Bean
	 * @param id
	 * @return
	 */
	BeanContext getBean(BeanIdentity id);

	/**
	 * 读取所有的Bean
	 * @return
	 */
	List<BeanContext> getBeans();

	/**
	 * 从实现反向获取一个BeanContext
	 * @param bean
	 * @return
	 */
	BeanContext findBeanContext(Object bean);

	/**
	 * 增加一个Bean, 当add失败后抛出异常
	 * @param bean
	 */
	void addBean(BeanContext bean);

	/**
	 * 新增或更新一个Bean的信息
	 * @param bean
	 */
	BeanContext putBean(BeanContext bean);

	/**
	 * 移除一个Bean
	 * @param id
	 */
	BeanContext removeBean(BeanIdentity id);

	/**
	 * 当Beans环境发生变化的时候得到通知
	 * @param
	 */
	void syncBeans(EventHandler<BeanContextModification> handler, Function<BeanContext, Boolean> filter);


	/**
	 * 删除一个SyncHandler
	 * @param handler
	 */
	void removeSyncHandler(EventHandler<BeanContextModification> handler);

	/**
	 * 使用Id获取一个Bean
	 * @param id
	 * @return
	 */
	default BeanContext getBean(String id) {
		return getBean(BeanIdentity.parseFrom(id));
	}

	/**
	 * 获取一个Bean的ServerRouter
	 * @param bundleName
	 * @return
	 */
	ServerRouter subscribeServerRouter(BeanContext bc, String bundleName, String protocol);

	/**
	 * 使用Service的反射获取一个Service
	 * @param interfaceClazz
	 * @param <E>
	 * @return
	 */
	default <E> E getService(Class<E> interfaceClazz) {
		ServiceInterface si = interfaceClazz.getAnnotation(ServiceInterface.class);
		if (si == null) {
			throw new IllegalArgumentException("Missing @ServiceInterface for:" + interfaceClazz.getName());
		}
		BeanContext ctx = getBean(si.id());
		if (ctx == null) {
			throw new IllegalArgumentException("Bean not found:" + si.id());
		}
		if (!interfaceClazz.isInstance(ctx.getBean())) {
			throw new IllegalArgumentException("Invalid type:" + ctx.getBean().getClass() + " for interface:" + interfaceClazz.getName());
		}
		return (E)ctx.getBean();
	}

	/**
	 * 使用Service的反射获取一个Service
	 * @param interfaceClazz
	 * @param <E>
	 * @return
	 */
	default <E> E getService(Class<E> interfaceClazz, String beanId) {
		BeanContext bc = getBean(beanId);
		if (bc == null) {
			return null;
		}
		Object bean = bc.getBean();
		return (E)bean;
	}

	CentralizedService getCentralizedService();

	String getEnv(String name);

	/**
	 * notify
	 * @param ctx
	 * @param type
	 * @return
	 */
	default void notify(BeanContext ctx, BeanContextModification.Action type) { }

	default BundleManager getBundleManager(){
		return null;
	}

}
