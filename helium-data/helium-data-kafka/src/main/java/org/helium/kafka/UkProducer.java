package org.helium.kafka;

import org.helium.framework.annotations.FieldLoaderType;
import org.helium.kafka.spi.producer.UkProducerLoader;

/**
 * 用于生产消息至kafka
 */
@FieldLoaderType(loaderType = UkProducerLoader.class)
public interface UkProducer {

    default void produce(byte[] content){
        return;
    }
}
