/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2011-2-22
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 的事件处理机制 
 * 
 * Created by Coral
 */
public final class Event<E>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Event.class);
	
	private AtomicReference<EventHandler<E>> listener0;
	private LinkedList<EventHandler<E>> listeners;
	private Object sender;
	
	public Event(Object sender)
	{
		this.sender = sender;
		listener0 = new AtomicReference<EventHandler<E>>();
	}
	
	/**
	 * 
	 * 增加一个事件处理器
	 * @param eventHandler
	 */
	public void addListener(EventHandler<E> eventHandler)
	{
		if (listener0.compareAndSet(null, eventHandler) == false) {
			synchronized (this) {
				if (listeners == null) {
					listeners = new LinkedList<EventHandler<E>>();
				}
				listeners.addLast(eventHandler);
			}
		}
	}
	
	/**
	 * 
	 * 移除一个事件触发器
	 * @param e
	 * @return
	 */
	public boolean removeListener(EventHandler<E> e)
	{
		boolean result = false;
		if (listeners != null) {
			synchronized (this) {
				result = listeners.remove(e);
			}
		}
		if (result) {
			return true;
		} else {
			return listener0.compareAndSet(e, null);
		}
	}

	/**
	 * 
	 * 是否注册了事件处理器
	 */
	public boolean hasListener()
	{
		return  listener0.get() != null || (listeners != null && listeners.size() > 0);
	}
	
	/**
	 * 
	 * 获取已注册的事件处理器的数量
	 * @return
	 */
	public int getListenerCount()
	{
		return (listener0.get() != null ? 1 : 0) + (listeners != null ? listeners.size() : 0);
	}
	/**
	 * 
	 * 触发所有事件
	 * @param args
	 */
	public void fireEvent(E args)
	{
		EventHandler<E> l0 = listener0.get();
		if (l0 != null) {
			try {
				LOGGER.trace("run handler-0 {}", args);
				l0.run(sender, args);
			} catch (Exception ex) {
				LOGGER.error("EventHandler failed", ex);
			}
		}
		
		if (listeners != null) {
			ArrayList<EventHandler<E>> list = new ArrayList<EventHandler<E>>();
			synchronized (this) {
				for (EventHandler<E> a: listeners) {
					list.add(a);
				}
			}
			for (EventHandler<E> handler: list) {
				try {
					LOGGER.trace("run handler {}", args);
					handler.run(sender, args);
				} catch (Exception ex) {
					LOGGER.error("EventHandler failed", ex);
				}				
			}
		}
	}

	/**
	 * 
	 * 获取所有的Handlers
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public EventHandler<E>[] getAllHandlers()
	{
		EventHandler<E>[] r = null;
		synchronized (this) {
			EventHandler<E> l0 = listener0.get();
			if (l0 != null) {
				int n = 1 + (listeners != null ? listeners.size() : 0);
				r = new EventHandler[n];
				r[0] = l0;
				
				if (listeners != null) {
					for (int i = 0; i < listeners.size(); i++) {
						r[i + 1] = listeners.get(i);
					}
				}
			} else {
				if (listeners != null) {
					r = new EventHandler[listeners.size()];
					listeners.toArray(r);
				}
			}
		}
		return r;
	}


}
