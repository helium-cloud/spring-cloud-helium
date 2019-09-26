package org.helium.cloud.task.rpc;

import java.io.Serializable;

public class TaskInvokerArgs implements Serializable {
	private String id;
	private String event;
	private Object args;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Object getArgs() {
		return args;
	}

	public void setArgs(Object args) {
		this.args = args;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	@Override
	public String toString() {
		if (args != null){
			return args.toString();
		}
		return super.toString();
	}
}
