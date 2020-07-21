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
public class EntityLogWriter extends LogWriterAdapter {


	@Override
	protected void doLog(LogMessage logMessage) {

	}
}
