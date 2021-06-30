package org.helium.perfmon.config;


import org.helium.perfmon.controller.GetCategoriesController;
import org.helium.perfmon.controller.PerfmonController;
import org.helium.perfmon.controller.PullController;
import org.helium.perfmon.controller.SubscribeController;
import org.helium.perfmon.recoder.PerfmonRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PerfmonConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(PerfmonConfiguration.class);


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
	public PerfmonRecorder perfmonRecorder() {
		return new PerfmonRecorder();
	}

	@Bean
	public PerfmonController  perfmonController(){
		return new PerfmonController();
	}
}
