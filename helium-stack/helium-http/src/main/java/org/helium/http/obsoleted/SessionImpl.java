//package org.helium.http.servlet.extension.spi;
//
//import org.glassfish.grizzly.http.server.Session;
//import org.glassfish.grizzly.localization.LogMessages;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.servlet.http.HttpSession;
//import javax.servlet.http.HttpSessionBindingEvent;
//import javax.servlet.http.HttpSessionBindingListener;
//import javax.servlet.http.HttpSessionContext;
//import java.util.Collections;
//import java.util.Enumeration;
//
///**
// * @author coral
// * @version 创建时间：2014年9月24日 类说明
// */
//
//class SessionImpl implements HttpSession {
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(SessionImpl.class);
//
//	private Session session;
//	private Long creationTime;
//	private Long lastAccessed;
//	private boolean isNew = true;
//
//	public SessionImpl() {
//		creationTime = System.currentTimeMillis();
//		lastAccessed = creationTime;
//	}
//
//	@Override
//	public long getCreationTime() {
//		return creationTime;
//	}
//
//	@Override
//	public String getId() {
//		return session.getIdInternal();
//	}
//
//	@Override
//	public long getLastAccessedTime() {
//		return lastAccessed;
//	}
//
//	protected void access() {
//		lastAccessed = System.currentTimeMillis();
//		session.setTimestamp(lastAccessed);
//		isNew = false;
//	}
//
//	@Override
//	public javax.servlet.ServletContext getServletContext() {
//		throw new IllegalArgumentException("不被支持");
//	}
//
//	@Override
//	public void setMaxInactiveInterval(int sessionTimeout) {
//		if (sessionTimeout < 0) {
//			sessionTimeout = -1;
//		} else {
//			sessionTimeout = sessionTimeout * 1000;
//		}
//
//		session.setSessionTimeout(sessionTimeout);
//	}
//
//	@Override
//	public int getMaxInactiveInterval() {
//		long sessionTimeout = session.getSessionTimeout();
//		if (sessionTimeout < 0) {
//			return -1;
//		}
//
//		sessionTimeout /= 1000;
//		if (sessionTimeout > Integer.MAX_VALUE) {
//			throw new IllegalArgumentException(sessionTimeout + " cannot be cast to int.");
//		}
//
//		return (int) sessionTimeout;
//	}
//
//	@Override
//	public HttpSessionContext getSessionContext() {
//		return null;
//	}
//
//	@Override
//	public Object getAttribute(String key) {
//		return session.getAttribute(key);
//	}
//
//	@Override
//	public Object getValue(String value) {
//		return session.getAttribute(value);
//	}
//
//	@Override
//	public Enumeration<String> getAttributeNames() {
//		return Collections.enumeration(session.attributes().keySet());
//	}
//
//	@Override
//	public String[] getValueNames() {
//		return session.attributes().entrySet().toArray(new String[session.attributes().size()]);
//	}
//
//	@Override
//	public void setAttribute(String key, Object value) {
//
//		if (value == null) {
//			removeAttribute(key);
//			return;
//		}
//
//		Object unbound = session.getAttribute(key);
//		session.setAttribute(key, value);
//
//		if ((unbound != null) && (unbound != value) && (unbound instanceof HttpSessionBindingListener)) {
//			try {
//				((HttpSessionBindingListener) unbound).valueUnbound(new HttpSessionBindingEvent(this, key));
//			} catch (Throwable t) {
//				LOGGER.warn(LogMessages.WARNING_GRIZZLY_HTTP_SERVLET_SESSION_LISTENER_UNBOUND_ERROR(unbound.getClass().getName()));
//			}
//		}
//		HttpSessionBindingEvent event = null;
//
//		if (value instanceof HttpSessionBindingListener) {
//			if (value != unbound) {
//				event = new HttpSessionBindingEvent(this, key, value);
//				try {
//					((HttpSessionBindingListener) value).valueBound(event);
//				} catch (Throwable t) {
//					LOGGER.warn(LogMessages.WARNING_GRIZZLY_HTTP_SERVLET_SESSION_LISTENER_BOUND_ERROR(value.getClass().getName()));
//				}
//			}
//		}
//
//	}
//
//	@Override
//	public void putValue(String key, Object value) {
//		setAttribute(key, value);
//	}
//
//	@Override
//	public void removeAttribute(String key) {
//		session.removeAttribute(key);
//	}
//
//	@Override
//	public void removeValue(String key) {
//		removeAttribute(key);
//	}
//
//	@Override
//	public synchronized void invalidate() {
//		session.setValid(false);
//		session.attributes().clear();
//
//		creationTime = 0L;
//		isNew = true;
//	}
//
//	@Override
//	public boolean isNew() {
//		return isNew;
//	}
//
//	protected void setSession(Session session) {
//		this.session = session;
//	}
//
//}
