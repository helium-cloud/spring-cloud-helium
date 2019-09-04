package org.helium.rpc.event;

/**
 * @author coral
 * @version 创建时间：2015年1月28日
 * 类说明
 */
public abstract class AbstractUpcEventListener<E extends UpcEvent> implements UpcEventListener<E> {


	@Override
	public void onTimeout() {


	}

	@Override
	public void onCancel() {

	}

}
