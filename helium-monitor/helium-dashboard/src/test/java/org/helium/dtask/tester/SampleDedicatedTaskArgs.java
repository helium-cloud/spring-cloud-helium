package org.helium.dtask.tester;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.type.DateTime;
import org.helium.framework.task.DedicatedTaskArgs;

/**
 * Created by Coral on 10/24/15.
 */
public class SampleDedicatedTaskArgs extends SuperPojo implements DedicatedTaskArgs {
	@Field(id = 1)
	private boolean callPutTask;

	@Field(id = 2)
	private String tag;

	@Field(id = 3)
	private int servicePid;

	@Field(id = 4)
	private int taskPid;

	@Field(id = 5)
	private String callbackUrl;

	@Field(id = 6)
	private int caseIndex;

	DateTime beginTime;

	DateTime endTime;

	boolean closed;

	public boolean isCallPutTask() {
		return callPutTask;
	}

	public void setCallPutTask(boolean callPutTask) {
		this.callPutTask = callPutTask;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getServicePid() {
		return servicePid;
	}

	public void setServicePid(int servicePid) {
		this.servicePid = servicePid;
	}

	public int getTaskPid() {
		return taskPid;
	}

	public void setTaskPid(int taskPid) {
		this.taskPid = taskPid;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public int getCaseIndex() {
		return caseIndex;
	}

	public void setCaseIndex(int caseIndex) {
		this.caseIndex = caseIndex;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
