package org.helium.framework.configuration;

/**
 * 可在容器中加载并管理的Bean
 * 必须按照以下模式实现
 * <code>
 *
 * </code>
 * Created by Coral on 7/31/15.
 */
public abstract class Configurator<E extends Configurator> {
	private E instance;
	public E getInstance() {
		return instance;
	}

	public Configurator(E instance) {
		this.instance = instance;
	}

	/**
	 * 重载值
	 */
	public abstract void reloadValues();
}
