package org.helium.kafka;

import org.helium.framework.annotations.FieldLoaderType;
import org.helium.kafka.entity.UkArgs;
import org.helium.kafka.spi.producer.UkProducerLoader;

/**
 * 用于生产消息至kafka
 */
@FieldLoaderType(loaderType = UkProducerLoader.class)
public interface UkProducer {
    /**
     * 生产一条消息
     *
     * @param ukArgs
     */
    default void produce(UkArgs ukArgs){
        return;
    }
    default void produce(byte[] content){
        return;
    }
}
