package org.helium.config;


import org.helium.cloud.task.autoconfigure.HeliumTaskConfig;
import org.helium.perfmon.controller.GetBeansControl;
import org.helium.perfmon.controller.GetCategoriesController;
import org.helium.perfmon.controller.PullController;
import org.helium.perfmon.controller.SubscribeController;
import org.helium.perfmon.recoder.PerfmonRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(HeliumTaskConfig.class)
public class PerfmonConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(PerfmonConfiguration.class);


	/**
	 * GetBeansControl
	 *
	 * @return
	 */
	@Bean
	public GetBeansControl getBeansControl() {
		return new GetBeansControl();
	}

	/**
	 * GetCategoriesController
	 *
	 * @return
	 */
	@Bean
	public GetCategoriesController getCategoriesController() {
		return new GetCategoriesController();
	}

	/**
	 * PullController
	 *
	 * @return
	 */
	@Bean
	public PullController pullController() {
		return new PullController();
	}

	/**
	 * SubscribeController
	 *
	 * @return
	 */
	@Bean
	public SubscribeController subscribeController() {
		return new SubscribeController();
	}

	/**
	 * SubscribeController
	 *
	 * @return
	 */
	@Bean
	public ResourcesAppConfig resourcesAppConfig() {
		return new ResourcesAppConfig();
	}



	@Bean
	public PerfmonRecorder perfmonRecorder(){
		return new PerfmonRecorder();
	}
}
