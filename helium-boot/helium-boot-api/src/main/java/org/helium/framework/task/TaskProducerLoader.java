package org.helium.framework.task;

import org.helium.framework.BeanContext;
import org.helium.framework.BeanIdentity;
import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.SetterNode;

/**
 *
 * Created by Coral on 5/5/15.
 */
public class TaskProducerLoader implements FieldLoader {

	@Override
	public Object loadField(SetterNode node) {
		TaskProducerFactory factory = BeanContext.getContextService().getService(TaskProducerFactory.class);

		String eventId = node.getInnerText();

		return factory.getProducer(eventId);
	}
}
