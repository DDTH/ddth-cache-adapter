package com.github.ddth.cacheadapter.test.cache;

import org.junit.Before;

import com.github.ddth.cacheadapter.redis.BaseRedisCache.KeyMode;
import com.github.ddth.cacheadapter.redis.RedisCacheFactory;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.2
 */
public class RedisCacheMonolisticTest extends BaseRedisCacheTest {

    public RedisCacheMonolisticTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(RedisCacheMonolisticTest.class);
    }

    @Before
    public void setUp() {
        RedisCacheFactory cf = buildRedisCacheFactory();
        cf.setKeyMode(KeyMode.MONOPOLISTIC);
        cf.init();
        cacheFactory = cf;
    }
}
