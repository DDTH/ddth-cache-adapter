package com.github.ddth.cacheadapter.test.ceserializer;

import org.junit.After;
import org.junit.Before;

import com.github.ddth.cacheadapter.utils.ces.KryoCacheEntrySerializer;
import com.github.ddth.cacheadapter.utils.compressor.JdkDeflateCompressor;

import junit.framework.Test;
import junit.framework.TestSuite;

public class KryoCacheEntrySerializerWithCompressionTest extends BaseCacheEntrySerializerTest {

    public KryoCacheEntrySerializerWithCompressionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(KryoCacheEntrySerializerWithCompressionTest.class);
    }

    @Before
    public void setUp() {
        cacheEntrySerializer = new KryoCacheEntrySerializer()
                .setCompressor(JdkDeflateCompressor.instance).init();
    }

    @After
    public void tearDown() {
    }
}
