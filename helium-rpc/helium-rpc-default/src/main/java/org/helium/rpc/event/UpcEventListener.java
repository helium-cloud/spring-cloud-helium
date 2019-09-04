package org.helium.rpc.event;

/**
 * 事件监听接口
 *
 * @author coral
 * @version 创建时间：2015年1月27日
 */
public interface UpcEventListener<E extends UpcEvent> {
	public abstract void execute(E e);

	public abstract void onTimeout();

	public abstract void onCancel();
}
