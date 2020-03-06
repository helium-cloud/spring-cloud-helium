package org.helium.framework.spring.autoconfigure;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

/**
 * {@link ApplicationListener} to trigger early initialization in a background thread of
 * time consuming tasks.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @since 1.3.0
 */
@Configuration
public class HeliumPreinitializer implements ApplicationListener<SpringApplicationEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger(HeliumPreinitializer.class);


	@Override
	public void onApplicationEvent(SpringApplicationEvent event) {
		LOGGER.info("HeliumPreinitializer.onApplicationEvent{}", event.getSource());


	}

	/**
	 * 解决 helium中使用springboot方法
	 */
	private void resolveAutoWire(SpringApplicationEvent event) {

	}

}
