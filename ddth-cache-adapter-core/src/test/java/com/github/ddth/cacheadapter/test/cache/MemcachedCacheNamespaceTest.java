package com.github.ddth.cacheadapter.test.cache;

import org.junit.Before;

import com.github.ddth.cacheadapter.memcached.XMemcachedCache.KeyMode;
import com.github.ddth.cacheadapter.memcached.XMemcachedCacheFactory;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.2
 */
public class MemcachedCacheNamespaceTest extends BaseMemcachedCacheTest {

    public MemcachedCacheNamespaceTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(MemcachedCacheNamespaceTest.class);
    }

    @Before
    public void setUp() {
        XMemcachedCacheFactory cf = buildMemcachedCacheFactory();
        cf.setKeyMode(KeyMode.NAMESPACE);
        cf.init();
        cacheFactory = cf;
    }

}
