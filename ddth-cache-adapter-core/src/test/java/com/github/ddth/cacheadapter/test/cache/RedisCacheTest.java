package com.github.ddth.cacheadapter.test.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;

import com.github.ddth.cacheadapter.AbstractCacheFactory;
import com.github.ddth.cacheadapter.redis.RedisCacheFactory;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.7.0
 */
public class RedisCacheTest extends BaseCacheTest {

    public RedisCacheTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(RedisCacheTest.class);
    }

    @Before
    public void setUp() {
        Map<String, Properties> cacheProperties = new HashMap<>();

        RedisCacheFactory cf = new RedisCacheFactory();
        cf.setCacheProperties(cacheProperties);
        cf.setDefaultCacheCapacity(DEFAULT_CACHE_CAPACITY);
        cf.setDefaultExpireAfterAccess(DEFAULT_EXPIRE_AFTER_ACCESS);
        cf.setDefaultExpireAfterWrite(DEFAULT_EXPIRE_AFTER_WRITE);

        cf.setRedisHostAndPort("localhost:6379");
        cf.init();
        cacheFactory = cf;
    }

    @After
    public void tearDown() {
        ((AbstractCacheFactory) cacheFactory).destroy();
    }
}
