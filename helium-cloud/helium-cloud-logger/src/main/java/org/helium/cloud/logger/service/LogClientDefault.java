package org.helium.cloud.logger.service;

import com.alibaba.fastjson.JSONObject;
import org.helium.framework.annotations.FieldSetter;
import org.helium.kafka.UkProducer;
import org.helium.logging.args.LogArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

public class LogClientDefault implements LogClient {


	@FieldSetter(value = "biz-logger")
	private UkProducer ukProducer;

	@Override
	public void log(LogArgs logArgs) {
		String content = JSONObject.toJSONString(logArgs, true);
		ukProducer.produce(content.getBytes());
	}
}
