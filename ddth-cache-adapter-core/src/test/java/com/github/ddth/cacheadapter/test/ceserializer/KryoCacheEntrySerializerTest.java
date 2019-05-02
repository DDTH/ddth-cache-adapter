package com.github.ddth.cacheadapter.test.ceserializer;

import org.junit.After;
import org.junit.Before;

import com.github.ddth.cacheadapter.utils.ces.KryoCacheEntrySerializer;

import junit.framework.Test;
import junit.framework.TestSuite;

public class KryoCacheEntrySerializerTest extends BaseCacheEntrySerializerTest {

    public KryoCacheEntrySerializerTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(KryoCacheEntrySerializerTest.class);
    }

    @Before
    public void setUp() {
        cacheEntrySerializer = new KryoCacheEntrySerializer().init();
    }

    @After
    public void tearDown() {
    }
}
