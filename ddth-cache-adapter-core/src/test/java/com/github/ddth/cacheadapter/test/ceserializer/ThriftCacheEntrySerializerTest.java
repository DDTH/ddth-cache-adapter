package com.github.ddth.cacheadapter.test.ceserializer;

import org.junit.After;
import org.junit.Before;

import com.github.ddth.cacheadapter.ces.ThriftCacheEntrySerializer;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ThriftCacheEntrySerializerTest extends BaseCacheEntrySerializerTest {

    public ThriftCacheEntrySerializerTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(ThriftCacheEntrySerializerTest.class);
    }

    @Before
    public void setUp() {
        cacheEntrySerializer = ThriftCacheEntrySerializer.instance;
    }

    @After
    public void tearDown() {
    }
}
