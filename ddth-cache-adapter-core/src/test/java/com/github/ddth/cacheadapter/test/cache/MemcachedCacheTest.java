package com.github.ddth.cacheadapter.test.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;

import com.github.ddth.cacheadapter.AbstractCacheFactory;
import com.github.ddth.cacheadapter.memcached.XMemcachedCacheFactory;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.2
 */
public class MemcachedCacheTest extends BaseCacheTest {

    public MemcachedCacheTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(MemcachedCacheTest.class);
    }

    @Before
    public void setUp() {
        Map<String, Properties> cacheProperties = new HashMap<>();

        XMemcachedCacheFactory cf = new XMemcachedCacheFactory();
        cf.setCacheProperties(cacheProperties);
        cf.setDefaultCacheCapacity(DEFAULT_CACHE_CAPACITY);
        cf.setDefaultExpireAfterAccess(DEFAULT_EXPIRE_AFTER_ACCESS);
        cf.setDefaultExpireAfterWrite(DEFAULT_EXPIRE_AFTER_WRITE);

        cf.setMemcachedHostsAndPorts("localhost:11211");
        cf.init();
        cacheFactory = cf;
    }

    @After
    public void tearDown() {
        ((AbstractCacheFactory) cacheFactory).destroy();
    }
}
