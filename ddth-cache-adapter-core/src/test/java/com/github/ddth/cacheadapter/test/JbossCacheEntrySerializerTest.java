package com.github.ddth.cacheadapter.test;

import org.junit.After;
import org.junit.Before;

import com.github.ddth.cacheadapter.ces.JbossCacheEntrySerializer;

import junit.framework.Test;
import junit.framework.TestSuite;

public class JbossCacheEntrySerializerTest extends BaseTest {

    public JbossCacheEntrySerializerTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(JbossCacheEntrySerializerTest.class);
    }

    @Before
    public void setUp() {
        cacheEntrySerializer = JbossCacheEntrySerializer.instance;
    }

    @After
    public void tearDown() {
    }
}
