package org.helium.dtask.tester;

import org.helium.framework.annotations.ServiceInterface;

/**
 * Created by Coral on 3/19/16.
 */
@ServiceInterface(id = "test:CollectService")
public interface TaskCollectService {
	void taskComplete(SampleDedicatedTaskArgs tag);

	void taskClosed(String tag);
}
