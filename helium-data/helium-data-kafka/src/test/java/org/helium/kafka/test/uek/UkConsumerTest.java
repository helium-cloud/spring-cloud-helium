package org.helium.kafka.test.uek;

import org.helium.kafka.UkConsumer;
import org.helium.kafka.UkConsumerHandler;
import org.helium.kafka.spi.consumer.UkConsumerManager;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by angel on 1/18/17.
 */
public class UkConsumerTest {

    public static void main(String args[]) throws IOException {
        String path = "helium-data-kafka/src/test/resources/ukconsumer.properties";
        Properties properties = new Properties();
        properties.load(new FileReader(path));
        UkConsumer ukconsumer = UkConsumerManager.INSTANCE.getKafkaConsumer("ukconsumer", properties);
        System.out.println("start listen...");
        ukconsumer.setAndRunHandler(new ConsumerHandler());

        UkConsumer ukconsumer1 = UkConsumerManager.INSTANCE.getKafkaConsumer("ukconsumer-2", properties);
        System.out.println("start listen...");
        ukconsumer1.setAndRunHandler(new ConsumerHandler());
    }
    static class ConsumerHandler implements UkConsumerHandler {
        @Override
        public void consumer(byte[] content) {
            System.out.println("content: " + new String(content));
        }

        @Override
        public boolean consumersyn(byte[] content) {
            System.out.println("syn content "+new String(content));
            return false;
        }

        @Override
        public boolean autocommit() {
            return true;
        }
    }
}
