package org.helium.cloud.logger.writer.impl;

import com.alibaba.fastjson.JSON;
import org.helium.cloud.logger.entity.LogBean;
import org.helium.cloud.logger.model.LogMessage;
import org.helium.framework.annotations.FieldSetter;
import org.helium.kafka.UkProducer;
import org.springframework.beans.factory.annotation.Value;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 将日志消息转发到kafka
 *
 * @author : zhangruibj@feinno.com
 * @since : 19-6-13 上午9:26
 */
public class KafkaLogWriter extends LogWriterAdapter {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    @FieldSetter("logger-kafka")
    private UkProducer ukProducer;

    @Value("${spring.application.name:im}")
    private String applicationName;

    /**
     * 子类实现,具体保存到哪
     *
     * @param logMessage 消息
     */
    @Override
    protected void doLog(LogMessage logMessage) {
        if (Objects.isNull(logMessage)) {
            return;
        }
        LogBean logBean = new LogBean();
        logBean.setLevel(logMessage.getLogLevel().name);
        logBean.setTimestamp(logMessage.getCreateTime());
        logBean.setResult(JSON.toJSONString(logMessage.getMethodResult()));
        logBean.setBusiness(logMessage.getClassName());
        logBean.setBusiness(applicationName);
        logBean.setType("annotation-log");
        logBean.setContent(JSON.toJSONString(logMessage));

        EXECUTOR_SERVICE.execute(() -> ukProducer.produce(JSON.toJSONBytes(logBean)));

    }

}
