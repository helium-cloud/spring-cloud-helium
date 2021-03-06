package org.helium.cloud.task.store;

import org.helium.cloud.task.TaskRouter;
import org.helium.cloud.task.TaskRouter.TaskRouteEntry;
import org.helium.framework.task.TaskProducer;
import org.helium.framework.task.TaskProducerFactory;


/**
 * Created by Coral on 7/5/15.
 * 生产者ID
 *
 */

public class TaskProducerFactoryImpl implements TaskProducerFactory {
	private TaskRouter router;

	public TaskProducerFactoryImpl() {
		router = new TaskRouter();
	}

	@Override
	public TaskProducer getProducer(String eventId) {
		TaskRouteEntry routeEntry = router.getEntry(eventId);
		if (routeEntry == null) {
			throw new IllegalArgumentException("Unknown event id=" + eventId);
		}
		return new TaskProducer() {
			@Override
			public void produce(Object args) {
				routeEntry.consume(args);
			}
		};
	}
}
