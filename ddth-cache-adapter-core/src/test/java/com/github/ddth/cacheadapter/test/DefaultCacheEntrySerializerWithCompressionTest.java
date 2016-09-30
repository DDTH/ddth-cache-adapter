package com.github.ddth.cacheadapter.test;

import org.junit.After;
import org.junit.Before;

import com.github.ddth.cacheadapter.ces.DefaultCacheEntrySerializer;
import com.github.ddth.cacheadapter.compressor.JdkDeflateCompressor;

import junit.framework.Test;
import junit.framework.TestSuite;

public class DefaultCacheEntrySerializerWithCompressionTest extends BaseTest {

    public DefaultCacheEntrySerializerWithCompressionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(DefaultCacheEntrySerializerWithCompressionTest.class);
    }

    @Before
    public void setUp() {
        cacheEntrySerializer = new DefaultCacheEntrySerializer()
                .setCompressor(JdkDeflateCompressor.instance);
    }

    @After
    public void tearDown() {
    }
}
