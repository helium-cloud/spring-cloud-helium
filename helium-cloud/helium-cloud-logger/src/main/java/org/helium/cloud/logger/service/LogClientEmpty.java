package org.helium.cloud.logger.service;

import com.alibaba.fastjson.JSONObject;
import org.helium.framework.annotations.FieldSetter;
import org.helium.kafka.UkProducer;
import org.helium.logging.args.LogArgs;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

public class LogClientEmpty implements LogClient {


	@Override
	public void log(LogArgs logArgs) {

	}
}
