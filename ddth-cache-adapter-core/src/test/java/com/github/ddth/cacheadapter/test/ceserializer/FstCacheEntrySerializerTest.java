package com.github.ddth.cacheadapter.test.ceserializer;

import org.junit.After;
import org.junit.Before;

import com.github.ddth.cacheadapter.utils.ces.FstCacheEntrySerializer;

import junit.framework.Test;
import junit.framework.TestSuite;

public class FstCacheEntrySerializerTest extends BaseCacheEntrySerializerTest {

    public FstCacheEntrySerializerTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(FstCacheEntrySerializerTest.class);
    }

    @Before
    public void setUp() {
        cacheEntrySerializer = new FstCacheEntrySerializer().init();
    }

    @After
    public void tearDown() {
    }
}
