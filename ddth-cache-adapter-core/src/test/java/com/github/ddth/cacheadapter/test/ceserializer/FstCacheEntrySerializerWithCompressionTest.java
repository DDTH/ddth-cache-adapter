package com.github.ddth.cacheadapter.test.ceserializer;

import org.junit.After;
import org.junit.Before;

import com.github.ddth.cacheadapter.utils.ces.FstCacheEntrySerializer;
import com.github.ddth.cacheadapter.utils.compressor.JdkDeflateCompressor;

import junit.framework.Test;
import junit.framework.TestSuite;

public class FstCacheEntrySerializerWithCompressionTest extends BaseCacheEntrySerializerTest {

    public FstCacheEntrySerializerWithCompressionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(FstCacheEntrySerializerWithCompressionTest.class);
    }

    @Before
    public void setUp() {
        cacheEntrySerializer = new FstCacheEntrySerializer()
                .setCompressor(JdkDeflateCompressor.instance);
    }

    @After
    public void tearDown() {
    }
}
