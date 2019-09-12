package org.helium.cloud.task.store;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import org.helium.cloud.task.api.DedicatedTaskContext;

public class TaskArgs extends SuperPojo implements Comparable<TaskArgs> {

	@Field(id = 1)
	private String id;
	@Field(id = 2)
	private String event;
	@Field(id = 3)
	private byte[] content;
	@Field(id = 4)
	private String tag;
	@Field(id = 5)
	private int priority;
	@Field(id = 6)
	private long tid;

	private Object object;

	DedicatedTaskContext ctx;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public long getTid() {
		return tid;
	}

	public void setTid(long tid) {
		this.tid = tid;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public DedicatedTaskContext getCtx() {
		return ctx;
	}

	public void setCtx(DedicatedTaskContext ctx) {
		this.ctx = ctx;
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
