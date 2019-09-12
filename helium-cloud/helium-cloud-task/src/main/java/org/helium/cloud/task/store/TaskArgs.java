package org.helium.cloud.task.store;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import org.helium.cloud.task.api.DedicatedTaskContext;

public class TaskArgs extends SuperPojo implements Comparable<TaskArgs> {

	@Field(id = 1)
	private String id;
	@Field(id = 2)
	private String eventName;
	@Field(id = 3)
	private byte[] argStr;
	@Field(id = 4)
	private String tag;
	@Field(id = 5)
	private int priority;

	private long tid;

	private Object object;

	DedicatedTaskContext ctx;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public byte[] getArgStr() {
		return argStr;
	}

	public void setArgStr(byte[] argStr) {
		this.argStr = argStr;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public long getTid() {
		return tid;
	}

	public void setTid(long tid) {
		this.tid = tid;
	}


	public DedicatedTaskContext getCtx() {
		return ctx;
	}

	public void setCtx(DedicatedTaskContext ctx) {
		this.ctx = ctx;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public int compareTo(TaskArgs o) {
		if (priority < o.priority) {
			return -1;
		} else {
			if (priority > o.priority) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}
