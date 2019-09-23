package org.helium.framework.spi.task;

import org.helium.framework.BeanContext;
import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.task.TaskProducer;
import org.helium.framework.task.TaskProducerFactory;
import org.helium.framework.task.TaskRouter;
import org.helium.framework.task.TaskRouter.TaskRouteEntry;

/**
 * Created by Coral on 7/5/15.
 */
@ServiceImplementation
public class TaskProducerFactoryImpl implements TaskProducerFactory {
	private TaskRouter router;

	public TaskProducerFactoryImpl() {
		router = new TaskRouter(BeanContext.getContextService());
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
