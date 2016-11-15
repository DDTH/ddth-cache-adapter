package com.github.ddth.cacheadapter.test;

import org.junit.After;
import org.junit.Before;

import com.github.ddth.cacheadapter.ces.FstCacheEntrySerializer;

import junit.framework.Test;
import junit.framework.TestSuite;

public class FstCacheEntrySerializerTest extends BaseTest {

    public FstCacheEntrySerializerTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(FstCacheEntrySerializerTest.class);
    }

    @Before
    public void setUp() {
        cacheEntrySerializer = FstCacheEntrySerializer.instance;
    }

    @After
    public void tearDown() {
    }
}
