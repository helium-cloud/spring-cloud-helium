package org.helium.cloud.task.router;

/**
 * Created by Coral on 9/12/15.
 */
public interface RemoteTaskInvoker {
	void invoke(Object args);
	void invokerCallBack(Object args);
}
