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

import org.helium.common.constants.CommonConstants;
import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigUtilsTest {
    @BeforeEach
    public void setUp() throws Exception {
        ConfigUtils.setProperties(null);
    }

    @AfterEach
    public void tearDown() throws Exception {
        ConfigUtils.setProperties(null);
    }

    @Test
    public void testIsNotEmpty() throws Exception {
        assertThat(ConfigUtils.isNotEmpty("abc"), is(true));
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertThat(ConfigUtils.isEmpty(null), is(true));
        assertThat(ConfigUtils.isEmpty(""), is(true));
        assertThat(ConfigUtils.isEmpty("false"), is(true));
        assertThat(ConfigUtils.isEmpty("FALSE"), is(true));
        assertThat(ConfigUtils.isEmpty("0"), is(true));
        assertThat(ConfigUtils.isEmpty("null"), is(true));
        assertThat(ConfigUtils.isEmpty("NULL"), is(true));
        assertThat(ConfigUtils.isEmpty("n/a"), is(true));
        assertThat(ConfigUtils.isEmpty("N/A"), is(true));
    }

    @Test
    public void testIsDefault() throws Exception {
        assertThat(ConfigUtils.isDefault("true"), is(true));
        assertThat(ConfigUtils.isDefault("TRUE"), is(true));
        assertThat(ConfigUtils.isDefault("default"), is(true));
        assertThat(ConfigUtils.isDefault("DEFAULT"), is(true));
    }

//    @Test
//    public void testMergeValues() {
//        List<String> merged = ConfigUtils.mergeValues(ThreadPool.class, "aaa,bbb,default.custom",
//                asList("fixed", "default.limited", "cached"));
//        assertEquals(asList("fixed", "cached", "aaa", "bbb", "default.custom"), merged);
//    }

//    @Test
//    public void testMergeValuesAddDefault() {
//        List<String> merged = ConfigUtils.mergeValues(ThreadPool.class, "aaa,bbb,default,zzz",
//                asList("fixed", "default.limited", "cached"));
//        assertEquals(asList("aaa", "bbb", "fixed", "cached", "zzz"), merged);
//    }

//    @Test
//    public void testMergeValuesDeleteDefault() {
//        List<String> merged = ConfigUtils.mergeValues(ThreadPool.class, "-default", asList("fixed", "default.limited", "cached"));
//        assertEquals(Collections.emptyList(), merged);
//    }
//
//    @Test
//    public void testMergeValuesDeleteDefault_2() {
//        List<String> merged = ConfigUtils.mergeValues(ThreadPool.class, "-default,aaa", asList("fixed", "default.limited", "cached"));
//        assertEquals(asList("aaa"), merged);
//    }
//
//    /**
//     * The user configures -default, which will delete all the default parameters
//     */
//    @Test
//    public void testMergeValuesDelete() {
//        List<String> merged = ConfigUtils.mergeValues(ThreadPool.class, "-fixed,aaa", asList("fixed", "default.limited", "cached"));
//        assertEquals(asList("cached", "aaa"), merged);
//    }

    @Test
    public void testReplaceProperty() throws Exception {
        String s = ConfigUtils.replaceProperty("1${a.b.c}2${a.b.c}3", Collections.singletonMap("a.b.c", "ABC"));
        assertEquals(s, "1ABC2ABC3");
        s = ConfigUtils.replaceProperty("1${a.b.c}2${a.b.c}3", Collections.<String, String>emptyMap());
        assertEquals(s, "123");
    }

    @Test
    public void testGetProperties1() throws Exception {
        try {
            System.setProperty(CommonConstants.helium_PROPERTIES_KEY, "properties.load");
            Properties p = ConfigUtils.getProperties();
            assertThat((String) p.get("a"), equalTo("12"));
            assertThat((String) p.get("b"), equalTo("34"));
            assertThat((String) p.get("c"), equalTo("56"));
        } finally {
            System.clearProperty(CommonConstants.helium_PROPERTIES_KEY);
        }
    }

    @Test
    public void testGetProperties2() throws Exception {
        System.clearProperty(CommonConstants.helium_PROPERTIES_KEY);
        Properties p = ConfigUtils.getProperties();
        assertThat((String) p.get("helium"), equalTo("properties"));
    }

    @Test
    public void testAddProperties() throws Exception {
        Properties p = new Properties();
        p.put("key1", "value1");
        ConfigUtils.addProperties(p);
        assertThat((String) ConfigUtils.getProperties().get("key1"), equalTo("value1"));
    }

    @Test
    public void testLoadPropertiesNoFile() throws Exception {
        Properties p = ConfigUtils.loadProperties("notExisted", true);
        Properties expected = new Properties();
        assertEquals(expected, p);

        p = ConfigUtils.loadProperties("notExisted", false);
        assertEquals(expected, p);
    }

    @Test
    public void testGetProperty() throws Exception {
        assertThat(ConfigUtils.getProperty("helium"), equalTo("properties"));
    }

    @Test
    public void testGetPropertyDefaultValue() throws Exception {
        assertThat(ConfigUtils.getProperty("not-exist", "default"), equalTo("default"));
    }

    @Test
    public void testGetPropertyFromSystem() throws Exception {
        try {
            System.setProperty("helium", "system");
            assertThat(ConfigUtils.getProperty("helium"), equalTo("system"));
        } finally {
            System.clearProperty("helium");
        }
    }

    @Test
    public void testGetSystemProperty() throws Exception {
        try {
            System.setProperty("helium", "system-only");
            assertThat(ConfigUtils.getSystemProperty("helium"), equalTo("system-only"));
        } finally {
            System.clearProperty("helium");
        }
    }

    @Test
    public void testLoadProperties() throws Exception {
        Properties p = ConfigUtils.loadProperties("helium.properties");
        assertThat((String)p.get("helium"), equalTo("properties"));
    }

    @Test
    public void testLoadPropertiesOneFile() throws Exception {
        Properties p = ConfigUtils.loadProperties("properties.load", false);

        Properties expected = new Properties();
        expected.put("a", "12");
        expected.put("b", "34");
        expected.put("c", "56");

        assertEquals(expected, p);
    }

    @Test
    public void testLoadPropertiesOneFileAllowMulti() throws Exception {
        Properties p = ConfigUtils.loadProperties("properties.load", true);

        Properties expected = new Properties();
        expected.put("a", "12");
        expected.put("b", "34");
        expected.put("c", "56");

        assertEquals(expected, p);
    }

    @Test
    public void testLoadPropertiesOneFileNotRootPath() throws Exception {
        Properties p = ConfigUtils.loadProperties("META-INF/helium/internal/org.helium.common.threadpool.ThreadPool", false);

        Properties expected = new Properties();
        expected.put("fixed", "org.helium.common.threadpool.support.fixed.FixedThreadPool");
        expected.put("cached", "org.helium.common.threadpool.support.cached.CachedThreadPool");
        expected.put("limited", "org.helium.common.threadpool.support.limited.LimitedThreadPool");
        expected.put("eager", "org.helium.common.threadpool.support.eager.EagerThreadPool");

        assertEquals(expected, p);
    }


    @Disabled("Not know why disabled, the original link explaining this was reachable.")
    @Test
    public void testLoadPropertiesMultiFileNotRootPathException() throws Exception {
        try {
            ConfigUtils.loadProperties("META-INF/services/StatusChecker", false);
            Assertions.fail();
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("only 1 META-INF/services/StatusChecker file is expected, but 2 helium.properties files found on class path:"));
        }
    }

    @Test
    public void testLoadPropertiesMultiFileNotRootPath() throws Exception {

        Properties p = ConfigUtils.loadProperties("META-INF/helium/internal/org.helium.common.status.StatusChecker", true);

        Properties expected = new Properties();
        expected.put("memory", "MemoryStatusChecker");
        expected.put("load", "LoadStatusChecker");
        expected.put("aa", "12");

//        assertEquals(expected, p);
    }

    @Test
    public void testGetPid() throws Exception {
        assertThat(ConfigUtils.getPid(), greaterThan(0));
    }

    @Test
    public void testPropertiesWithStructedValue() throws Exception {
        Properties p = ConfigUtils.loadProperties("parameters.properties", false);

        Properties expected = new Properties();
        expected.put("helium.parameters", "[{a:b},{c_.d: r*}]");

        assertEquals(expected, p);
    }
}
