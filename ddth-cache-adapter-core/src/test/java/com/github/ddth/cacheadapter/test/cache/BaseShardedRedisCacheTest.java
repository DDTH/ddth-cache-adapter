package com.github.ddth.cacheadapter.test.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.github.ddth.cacheadapter.redis.ShardedRedisCacheFactory;

import junit.framework.Test;
import junit.framework.TestSuite;
import redis.clients.jedis.ShardedJedis;

/**
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.2
 */
public abstract class BaseShardedRedisCacheTest extends BaseCacheTest {

    public BaseShardedRedisCacheTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(BaseShardedRedisCacheTest.class);
    }

    protected static class MyRedisCacheFactory extends ShardedRedisCacheFactory {
        public ShardedRedisCacheFactory init() {
            super.init();

            try (ShardedJedis jedis = getJedisPool().getResource()) {
                jedis.getAllShards().forEach(j -> j.flushAll());
            }

            return this;
        }
    }

    protected ShardedRedisCacheFactory buildRedisCacheFactory() {
        Map<String, Properties> cacheProperties = new HashMap<>();

        ShardedRedisCacheFactory cf = new MyRedisCacheFactory();
        cf.setCacheProperties(cacheProperties);
        cf.setDefaultCacheCapacity(DEFAULT_CACHE_CAPACITY);
        cf.setDefaultExpireAfterAccess(DEFAULT_EXPIRE_AFTER_ACCESS);
        cf.setDefaultExpireAfterWrite(DEFAULT_EXPIRE_AFTER_WRITE);
        cf.setRedisHostsAndPorts("localhost:7379,localhost:7380");

        return cf;
    }

}
