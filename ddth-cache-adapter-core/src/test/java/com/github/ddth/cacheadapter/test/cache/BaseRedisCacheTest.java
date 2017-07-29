package com.github.ddth.cacheadapter.test.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.github.ddth.cacheadapter.redis.RedisCacheFactory;

import junit.framework.Test;
import junit.framework.TestSuite;
import redis.clients.jedis.Jedis;

/**
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.2
 */
public abstract class BaseRedisCacheTest extends BaseCacheTest {

    public BaseRedisCacheTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(BaseRedisCacheTest.class);
    }

    protected static class MyRedisCacheFactory extends RedisCacheFactory {
        public RedisCacheFactory init() {
            super.init();

            try (Jedis jedis = getJedisPool().getResource()) {
                jedis.flushAll();
            }

            return this;
        }
    }

    protected RedisCacheFactory buildRedisCacheFactory() {
        Map<String, Properties> cacheProperties = new HashMap<>();

        RedisCacheFactory cf = new MyRedisCacheFactory();
        cf.setCacheProperties(cacheProperties);
        cf.setDefaultCacheCapacity(DEFAULT_CACHE_CAPACITY);
        cf.setDefaultExpireAfterAccess(DEFAULT_EXPIRE_AFTER_ACCESS);
        cf.setDefaultExpireAfterWrite(DEFAULT_EXPIRE_AFTER_WRITE);
        cf.setRedisHostAndPort("localhost:6379");

        return cf;
    }

}
