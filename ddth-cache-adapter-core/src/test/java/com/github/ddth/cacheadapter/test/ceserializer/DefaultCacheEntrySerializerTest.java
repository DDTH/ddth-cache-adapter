package com.github.ddth.cacheadapter.test.ceserializer;

import org.junit.After;
import org.junit.Before;

import com.github.ddth.cacheadapter.utils.ces.DefaultCacheEntrySerializer;

import junit.framework.Test;
import junit.framework.TestSuite;

public class DefaultCacheEntrySerializerTest extends BaseCacheEntrySerializerTest {

    public DefaultCacheEntrySerializerTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(DefaultCacheEntrySerializerTest.class);
    }

    @Before
    public void setUp() {
        cacheEntrySerializer = DefaultCacheEntrySerializer.instance;
    }

    @After
    public void tearDown() {
    }
}
