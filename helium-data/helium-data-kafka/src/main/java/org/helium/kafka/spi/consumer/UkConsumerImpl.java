package org.helium.kafka.spi.consumer;


import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.helium.kafka.UkConsumer;
import org.helium.kafka.UkConsumerHandler;
import org.helium.kafka.UkProducer;
import org.helium.kafka.entity.Recurring;
import org.helium.kafka.spi.KafkaCounters;
import org.helium.kafka.spi.producer.UkProducerManager;
import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.perfmon.Stopwatch;
import org.helium.threading.ExecutorFactory;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executor;

/**
 * kafka消费者
 */
public class UkConsumerImpl implements UkConsumer {

    private final static Logger LOGGER = LoggerFactory.getLogger(UkConsumerImpl.class);
    private String kafkaConf;
    private Properties properties;
    private List<ConsumerConnector> connectorList = new ArrayList<>();
    private Executor executor = null;
    private int threadNum = 2;
    private KafkaCounters counters;
    private static final String threadName = "KafkaConsumer-";

    public UkConsumerImpl(String kafkaConf, Properties properties) {
        // Load Config
        this.kafkaConf = kafkaConf;
        this.properties = properties;
        //初始化线程数
        threadNum = Integer.parseInt(properties.getProperty("consumerThread", "2"));
        //初始化线程池
        executor = ExecutorFactory.newFixedExecutor(threadName + kafkaConf, threadNum, threadNum);
        //初始化连接池
        for (int i = 0; i < threadNum; i++) {
            ConsumerConnector consumerConnector = createConsumerConnector(properties);
            connectorList.add(consumerConnector);
        }
        this.counters = PerformanceCounterFactory.getCounters(KafkaCounters.class, kafkaConf);
    }


    @Override
    public void setAndRunHandler(UkConsumerHandler ukConsumerHandler) {
        for (int i = 0; i < threadNum; i++) {
            ConsumerThread consumerThread = new ConsumerThread(properties, connectorList.get(i), ukConsumerHandler);
            executor.execute(consumerThread);
        }
    }

    @Override
    public Properties getConsumerProperties() {
        return properties;
    }


    private synchronized ConsumerConnector createConsumerConnector(Properties prop) {
        LOGGER.info("create consumer producer, config :{}", prop.toString());
        ConsumerConfig consumerConfig = new ConsumerConfig(prop);
        return Consumer.createJavaConsumerConnector(consumerConfig);
    }

    /**
     * 内部匿名类创建消费者线程
     * 消费byte[]类型
     */
    class ConsumerThread extends Thread {
        Properties properties = null;
        ConsumerConnector consumerConnector = null;
        UkConsumerHandler ukConsumerHandler = null;

        ConsumerThread(Properties properties, ConsumerConnector consumerConnector, UkConsumerHandler ukConsumerHandler) {
            this.properties = properties;
            this.consumerConnector = consumerConnector;
            this.ukConsumerHandler = ukConsumerHandler;
        }

        @Override
        public void run() {
            Map<String, Integer> topicMap = new HashMap<>();
            String topic = properties.getProperty("topic", "");
            topicMap.put(topic, 1);
            Map<String, List<KafkaStream<byte[], byte[]>>> streamMap = consumerConnector.createMessageStreams(topicMap);
            KafkaStream<byte[], byte[]> stream = streamMap.get(topic).get(0);
            ConsumerIterator<byte[], byte[]> it = stream.iterator();
            while (true) {
                try {
                    if (it.hasNext()) {
                        counters.getQps().increase();
                        Stopwatch watch = counters.getTx().begin();
                        byte[] message = it.next().message();
                        if (ukConsumerHandler.autocommit()) {
                            ukConsumerHandler.consumer(message);
                            watch.end();
                        } else {
                            boolean result = ukConsumerHandler.consumersyn(message);
                            if (result) {
                                consumerConnector.commitOffsets();
                                watch.end();
                                LOGGER.info("consumer commited..");
                            } else {
                                watch.fail("retry to put kafka");
                                //TODO 待优化，消费失败重新写入kafka
                                String recurring = properties.getProperty("recurring");
                                String producerPort = properties.getProperty("producerPort");
                                if (!StringUtils.isNullOrEmpty(recurring)&&!StringUtils.isNullOrEmpty(producerPort)) {
                                    if (Recurring.ATLEASTONCE.equals(recurring)) {
                                        Thread.sleep(2000);
                                        new Thread(() -> {
                                            LOGGER.info("consume error,try to produce again.");
                                            Properties producerPro = new Properties();
                                            String property = properties.getProperty("zookeeper.connect");
                                            String s = property.substring(0,property.indexOf(":")+1);
                                            producerPro.setProperty("bootstrap.servers",s+producerPort);
                                            producerPro.setProperty("key.serializer", properties.getProperty("key.serializer"));
                                            producerPro.setProperty("value.serializer", properties.getProperty("value.serializer"));
                                            Producer producer = new KafkaProducer<String,byte[]>(producerPro);
                                            producer.send(new ProducerRecord(topic, message));
                                            producer.close();
                                        }).start();
                                    }
                                }
                                LOGGER.info("consumer not commit");
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("ConsumerThread error:{}", e);
                }

            }
        }
    }
}
