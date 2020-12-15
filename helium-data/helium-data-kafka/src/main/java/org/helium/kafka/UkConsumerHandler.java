package org.helium.kafka;

/**
 * 消费者模块
 */
public interface UkConsumerHandler {

	/**
	 * 消费ByteArrayDeserializer,异步消费
	 *
	 * @param content
	 */
	void consumer(byte[] content);

	/**
	 * 同步消费   ,配置文件:auto.commit.enable=false
	 *
	 * @param content
	 * @return
	 */
	default boolean consumersyn(byte[] content) {
		return true;
	}

	;

	/**
	 * 是否异步消费同步消息
	 *
	 * @return
	 */
	default boolean autocommit() {
		return true;
	}
}
