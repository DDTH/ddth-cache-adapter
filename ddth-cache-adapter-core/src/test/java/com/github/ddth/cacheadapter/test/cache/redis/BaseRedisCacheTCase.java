package com.github.ddth.cacheadapter.test.cache.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.github.ddth.cacheadapter.cacheimpl.redis.RedisCacheFactory;
import com.github.ddth.cacheadapter.test.cache.BaseCacheTCase;

import redis.clients.jedis.Jedis;

/**
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.2
 */
public abstract class BaseRedisCacheTCase extends BaseCacheTCase {

    protected static class MyRedisCacheFactory extends RedisCacheFactory {
        public RedisCacheFactory init() {
            super.init();

            try (Jedis jedis = getJedisConnector().getJedis()) {
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
