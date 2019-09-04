package org.helium.dtask.tester;

import org.helium.framework.annotations.ServiceInterface;

/**
 * Created by Coral on 3/19/16.
 */
@ServiceInterface(id = "test:TaskLaunchService")
public interface TaskLaunchService {
	void fireTask(SampleDedicatedTaskArgs args);
}
