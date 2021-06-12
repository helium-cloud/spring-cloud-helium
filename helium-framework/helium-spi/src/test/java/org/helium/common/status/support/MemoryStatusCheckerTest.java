/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.helium.common.status.support;

import org.hamcrest.Matchers;
import org.helium.common.status.Status;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.MatcherAssert.assertThat;

public class MemoryStatusCheckerTest {
    private static final Logger logger = LoggerFactory.getLogger(MemoryStatusCheckerTest.class);

    @Test
    public void test() throws Exception {
        MemoryStatusChecker statusChecker = new MemoryStatusChecker();
        Status status = statusChecker.check();
        assertThat(status.getLevel(), anyOf(Matchers.is(Status.Level.OK), Matchers.is(Status.Level.WARN)));
        logger.info("memory status level: " + status.getLevel());
        logger.info("memory status message: " + status.getMessage());
    }
}
