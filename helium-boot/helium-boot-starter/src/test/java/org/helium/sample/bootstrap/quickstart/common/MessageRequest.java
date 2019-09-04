package org.helium.sample.bootstrap.quickstart.common;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import org.helium.threading.Future;

public class MessageRequest extends SuperPojo {
	@Field(id = 1)
	private String mobile;
	@Field(id = 2)
	private String type;
	@Field(id = 3)
	private int priority;

	private Future<MessageResponse> messageResponseFuture;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}


	public Future<MessageResponse> getMessageResponseFuture() {
		return messageResponseFuture;
	}

	public void setMessageResponseFuture(Future<MessageResponse> messageResponseFuture) {
		this.messageResponseFuture = messageResponseFuture;
	}
}
