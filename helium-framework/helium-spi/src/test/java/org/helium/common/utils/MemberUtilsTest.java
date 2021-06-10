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
package org.helium.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link MemberUtils} Test
 *
 * @since 2.7.6
 */
public class MemberUtilsTest {

    @Test
    public void testIsStatic() throws NoSuchMethodException {

        Assertions.assertFalse(MemberUtils.isStatic(getClass().getMethod("testIsStatic")));
        Assertions.assertTrue(MemberUtils.isStatic(getClass().getMethod("staticMethod")));
        Assertions.assertTrue(MemberUtils.isPrivate(getClass().getDeclaredMethod("privateMethod")));
        Assertions.assertTrue(MemberUtils.isPublic(getClass().getMethod("publicMethod")));
    }

    public static void staticMethod() {

    }

    private void privateMethod() {

    }

    public void publicMethod() {

    }
}
