package com.github.ddth.cacheadapter.test.cache.shardedredis;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.github.ddth.cacheadapter.cacheimpl.redis.ShardedRedisCacheFactory;
import com.github.ddth.cacheadapter.test.cache.BaseCacheTCase;

import redis.clients.jedis.ShardedJedis;

/**
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.2
 */
public abstract class BaseShardedRedisCacheTCase extends BaseCacheTCase {

    protected static class MyRedisCacheFactory extends ShardedRedisCacheFactory {
        public ShardedRedisCacheFactory init() {
            super.init();

            try (ShardedJedis jedis = getJedisConnector().getShardedJedis()) {
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
        cf.setRedisHostsAndPorts("localhost:6379,localhost:6380");

        return cf;
    }

}
