package org.helium.kafka;

import org.helium.framework.annotations.FieldLoaderType;
import org.helium.kafka.spi.consumer.UkConsumerLoader;

import java.util.Properties;

/**
 * 用于消息kafka中的消息
 */
@FieldLoaderType(loaderType = UkConsumerLoader.class)
public interface UkConsumer {

	Properties getConsumerProperties();

	void setAndRunHandler(UkConsumerHandler ukConsumerHandler);
}
