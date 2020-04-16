/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.helium.cloud.common.autoconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Dubbo Welcome Logo {@link ApplicationListener}
 *
 * @see ApplicationListener
 * @since 2.7.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 20 + 1)  // After LoggingApplicationListener#DEFAULT_ORDER
public class LogoApplicationListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static AtomicBoolean processed = new AtomicBoolean(false);

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {

        // Skip if processed before, prevent duplicated execution in Hierarchical ApplicationContext
        if (processed.get()) {
            return;
        }

        /**
         * Gets Logger After LoggingSystem configuration ready
         * @see LoggingApplicationListener
         */
        final Logger logger = LoggerFactory.getLogger(getClass());

        String bannerText = buildBannerText();

        if (logger.isInfoEnabled()) {
            logger.info(bannerText);
        } else {
            System.out.print(bannerText);
        }

        // mark processed to be true
        processed.compareAndSet(false, true);
    }

    String buildBannerText() {

        StringBuilder bannerTextBuilder = new StringBuilder();

        bannerTextBuilder
                .append("\n"+
                		" __                  _                         ___  _                    _                       _  _                   \n" +
						"/ _\\    _ __   _ __ (_) _ __    __ _          / __\\| |  ___   _   _   __| |         /\\  /\\  ___ | |(_) _   _  _ __ ___  \n" +
						"\\ \\    | '_ \\ | '__|| || '_ \\  / _` | _____  / /   | | / _ \\ | | | | / _` | _____  / /_/ / / _ \\| || || | | || '_ ` _ \\ \n" +
						"_\\ \\   | |_) || |   | || | | || (_| ||_____|/ /___ | || (_) || |_| || (_| ||_____|/ __  / |  __/| || || |_| || | | | | |\n" +
						"\\__/   | .__/ |_|   |_||_| |_| \\__, |       \\____/ |_| \\___/  \\__,_| \\__,_|       \\/ /_/   \\___||_||_| \\__,_||_| |_| |_|\n" +
						"       |_|                     |___/                                                                                    ");
		bannerTextBuilder.append("\n");
		bannerTextBuilder.append(" :: spring-cloud-helium:3.1.x-release \n");
        return bannerTextBuilder.toString();

    }

}
