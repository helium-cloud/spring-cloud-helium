package org.helium.framework;

/**
 * Created by Coral on 7/26/15.
 */
public class BeanContextModification {
	public enum Action {
		INSERT,
		UPDATE,
		DELETE,
	}
	private Action action;
	private BeanContext beanContext;
	// private String reason;

	public BeanContextModification(Action action, BeanContext beanContext) {
		this.action = action;
		this.beanContext = beanContext;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public BeanContext getBeanContext() {
		return beanContext;
	}

	public void setBeanContext(BeanContext beanContext) {
		this.beanContext = beanContext;
	}

//	public String getReason() {
//		return reason;
//	}
//
//	public void setReason(String reason) {
//		this.reason = reason;
//	}
}
