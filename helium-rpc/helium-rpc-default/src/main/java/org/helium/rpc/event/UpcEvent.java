package org.helium.rpc.event;

/**
 * 事件接口,所有事件继承自此接口
 *
 * @author coral
 * @version 创建时间：2015年1月27日
 */
public interface UpcEvent {
	/**
	 * 事件名称
	 *
	 * @return
	 */
	String getEventName();
}
