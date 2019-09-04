package org.helium.kafka.spi.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.helium.framework.BeanContext;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.kafka.UkProducer;
import org.helium.kafka.spi.KafkaCounters;
import org.helium.perfmon.PerformanceCounterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * kafka生产者管理
 */
public class UkProducerManager {

    /**
     * 配置
     */
    private ConfigProvider configProvider;

    /**
     * producer缓存
     */
    private static final Map<String, UkProducer> producers = new ConcurrentHashMap<>();

    /**
     * kafka配置路径
     */
    private static final String KAFKA_CONFIG_PATH = "kafka" + File.separator;

    public static final UkProducerManager INSTANCE = new UkProducerManager();
    private static final Logger LOGGER = LoggerFactory.getLogger(UkProducerManager.class);


    /**
     * 初始化对象
     */
    private UkProducerManager() {
        if (BeanContext.getContextService() != null) {
            configProvider = BeanContext.getContextService().getService(ConfigProvider.class);
        }
    }

    /**
     *  采用config-provider
     *
     * @param kafkaConf
     * @return
     */
    public UkProducer getKafkaProducer(String kafkaConf) {

        try {
            String content = configProvider.loadText(KAFKA_CONFIG_PATH + kafkaConf + ".properties");
            return getKafkaProducer(kafkaConf, content);
        } catch (Exception e) {
            LOGGER.error("getKafkaConsumer Exception:{}", kafkaConf, e);
        }
        return null;

    }
    /**
     * 获取指定kafka配置下的消费者配置
     *
     * @param kafkaConf
     * @return
     */
    public UkProducer getKafkaProducer(String kafkaConf, String content) {

        try {
            UkProducer producer = producers.get(kafkaConf);
            if (producer != null) {
                LOGGER.info("producer cache has not found, conf: {}", kafkaConf);
                return producer;
            }
            Properties properties = new Properties();
            properties.load(new ByteArrayInputStream(content.getBytes()));
            return getKafkaProducer(kafkaConf, properties);
        } catch (Exception e) {
            LOGGER.error("getKafkaProducer Exception:{}", kafkaConf, e);
        }
        return null;

    }

    /**
     * 获取指定kafka配置下的消费者配置
     *
     * @param kafkaConf
     * @return
     */
    public UkProducer getAndUpdateKafkaProducer(String kafkaConf, String content) {

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes());
            Properties prop = new Properties();
            prop.load(inputStream);
            return getKafkaProducer(kafkaConf, prop);
        } catch (Exception e) {
            LOGGER.error("getKafkaConsumer Exception:{}", kafkaConf, e);
        }
        return null;

    }

    public UkProducer getKafkaProducer(String kafkaConf, Properties prop) {
        //
        //环境变量添加，需要输入配置文件的路径
        // 从相对路径拿取/kafka/kafka_client_jaas.conf
        if (prop.getProperty("java.security.auth.login.config") !=  null){
            if (configProvider !=null){
                System.setProperty("java.security.auth.login.config", prop.getProperty("java.security.auth.login.config"));
            }
        }
        // Load Config
        Producer pro = createProducer(prop);
        UkProducer p = new UkProducerImpl(pro, kafkaConf, prop);
        producers.put(kafkaConf, p);
        LOGGER.info("producer cache hasn't, put producer into cache, conf: {}", kafkaConf);
        return p;
    }

    private synchronized Producer createProducer(Properties prop) {
        LOGGER.info("create Kafka producer, config :{}", prop.toString());
        return new KafkaProducer(prop);
    }
}

