package com.github.ddth.cacheadapter.test.ceserializer;

import org.junit.After;
import org.junit.Before;

import com.github.ddth.cacheadapter.utils.ces.ThriftCacheEntrySerializer;
import com.github.ddth.cacheadapter.utils.compressor.JdkDeflateCompressor;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ThriftCacheEntrySerializerWithCompressionTest extends BaseCacheEntrySerializerTest {

    public ThriftCacheEntrySerializerWithCompressionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(ThriftCacheEntrySerializerWithCompressionTest.class);
    }

    @Before
    public void setUp() {
        cacheEntrySerializer = new ThriftCacheEntrySerializer()
                .setCompressor(JdkDeflateCompressor.instance);
    }

    @After
    public void tearDown() {
    }
}
