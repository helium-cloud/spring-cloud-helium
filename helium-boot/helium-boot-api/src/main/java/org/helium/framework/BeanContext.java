package org.helium.framework;

import org.helium.framework.entitys.BeanConfiguration;
import org.helium.framework.entitys.BeanNode;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 用于对Bean进行操作的Context
 * Created by Coral on 7/19/15.
 */
public interface BeanContext {
	/**
	 * 是否本机
	 * @return
	 */
	boolean isLocal();

	/**
	 * 获取BeanId
	 * @return
	 */
	BeanIdentity getId();


	/**
	 * 获取Bean类型
	 * @return
	 */
	BeanType getType();

	/**
	 * 获取Bean实例
	 * @return
	 */
	Object getBean();

	/**
	 * 获取配置
	 * @return
	 */
	BeanConfiguration getConfiguration();

	/**
	 * 获取状态
	 * @return
	 */
	BeanContextState getState();

	/**
	 * 获取错误信息
	 * @return
	 */
	Throwable getLastError();

	/**
	 *  @param key
	 * @param value
	 */
	Object putAttachment(String key, Object value);

	/**
	 *
	 * @param key
	 * @return
	 */
	Object getAttachment(String key);

	/**
	 * 默认方法, 使用对象类型作为Key
	 * @param object
	 * @param <E>
	 * @return
	 */
	default <E> E putAttachment(E object) {
		if (object == null) {
			throw new NullPointerException("putAttachment(null)");
		}
		return (E)putAttachment(object.getClass().getName(), object);
	}

	/**
	 * 默认方法, 使用传入类型作为Key, 适用于接口放入各种子类的场合
	 * @param object
	 * @param <E>
	 * @return
	 */
	default <E> E putAttachment(Class<E> clazz, Object object) {
		if (object == null) {
			throw new NullPointerException("putAttachment(null)");
		}
		return (E)putAttachment(clazz.getName(), object);
	}

	/**
	 * 按照特定类型名称获取Key
	 * @param clazz
	 * @param <E>
	 * @return
	 */
	default <E> E getAttachment(Class<E> clazz) {
		String key = clazz.getName();
		return (E)getAttachment(key);
	}

	/*
	 * 静态实现
	 */
	AtomicReference<BeanContextService> contextService = new AtomicReference<>();
	static void initializeService(BeanContextService service) {
		if (!contextService.compareAndSet(null, service)) {
			throw new IllegalArgumentException("already initialize");
		}
	}
	static BeanContextService getContextService() {
		return contextService.get();
	}
}