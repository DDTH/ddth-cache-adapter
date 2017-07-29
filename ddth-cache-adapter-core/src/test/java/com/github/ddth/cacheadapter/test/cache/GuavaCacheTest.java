package com.github.ddth.cacheadapter.test.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;

import com.github.ddth.cacheadapter.guava.GuavaCacheFactory;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.2
 */
public class GuavaCacheTest extends BaseCacheTest {

    public GuavaCacheTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(GuavaCacheTest.class);
    }

    @Before
    public void setUp() {
        Map<String, Properties> cacheProperties = new HashMap<>();

        GuavaCacheFactory cf = new GuavaCacheFactory();
        cf.setCacheProperties(cacheProperties);
        cf.setDefaultCacheCapacity(DEFAULT_CACHE_CAPACITY);
        cf.setDefaultExpireAfterAccess(DEFAULT_EXPIRE_AFTER_ACCESS);
        cf.setDefaultExpireAfterWrite(DEFAULT_EXPIRE_AFTER_WRITE);
        cf.init();
        cacheFactory = cf;
    }

}
