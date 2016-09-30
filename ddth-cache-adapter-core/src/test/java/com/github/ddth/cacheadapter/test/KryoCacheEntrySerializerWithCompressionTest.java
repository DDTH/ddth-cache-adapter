package com.github.ddth.cacheadapter.test;

import org.junit.After;
import org.junit.Before;

import com.github.ddth.cacheadapter.ces.KryoCacheEntrySerializer;
import com.github.ddth.cacheadapter.compressor.JdkDeflateCompressor;

import junit.framework.Test;
import junit.framework.TestSuite;

public class KryoCacheEntrySerializerWithCompressionTest extends BaseTest {

    public KryoCacheEntrySerializerWithCompressionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(KryoCacheEntrySerializerWithCompressionTest.class);
    }

    @Before
    public void setUp() {
        cacheEntrySerializer = new KryoCacheEntrySerializer()
                .setCompressor(JdkDeflateCompressor.instance);
    }

    @After
    public void tearDown() {
    }
}
