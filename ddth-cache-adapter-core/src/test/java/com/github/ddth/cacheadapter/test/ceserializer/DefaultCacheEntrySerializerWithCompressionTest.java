package com.github.ddth.cacheadapter.test.ceserializer;

import org.junit.After;
import org.junit.Before;

import com.github.ddth.cacheadapter.utils.ces.DefaultCacheEntrySerializer;
import com.github.ddth.cacheadapter.utils.compressor.JdkDeflateCompressor;

import junit.framework.Test;
import junit.framework.TestSuite;

public class DefaultCacheEntrySerializerWithCompressionTest extends BaseCacheEntrySerializerTest {

    public DefaultCacheEntrySerializerWithCompressionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(DefaultCacheEntrySerializerWithCompressionTest.class);
    }

    @Before
    public void setUp() {
        cacheEntrySerializer = new DefaultCacheEntrySerializer()
                .setCompressor(JdkDeflateCompressor.instance).init();
    }

    @After
    public void tearDown() {
    }
}
