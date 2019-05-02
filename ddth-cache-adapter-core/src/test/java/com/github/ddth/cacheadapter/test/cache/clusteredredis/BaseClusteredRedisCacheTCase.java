package com.github.ddth.cacheadapter.test.cache.clusteredredis;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.github.ddth.cacheadapter.cacheimpl.redis.ClusteredRedisCacheFactory;
import com.github.ddth.cacheadapter.test.cache.BaseCacheTCase;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.2
 */
public abstract class BaseClusteredRedisCacheTCase extends BaseCacheTCase {

    protected static class MyRedisCacheFactory extends ClusteredRedisCacheFactory {
        public MyRedisCacheFactory init() {
            super.init();

            JedisCluster jedis = getJedisConnector().getJedisCluster();
            jedis.getClusterNodes().forEach((n, p) -> {
                try (Jedis j = p.getResource()) {
                    j.flushAll();
                } catch (Exception e) {
                }
            });
            return this;
        }
    }

    /*
     * Use https://github.com/Grokzen/docker-redis-cluster for testing
     */
    protected ClusteredRedisCacheFactory buildRedisCacheFactory() {
        Map<String, Properties> cacheProperties = new HashMap<>();

        ClusteredRedisCacheFactory cf = new MyRedisCacheFactory();
        cf.setCacheProperties(cacheProperties);
        cf.setDefaultCacheCapacity(DEFAULT_CACHE_CAPACITY);
        cf.setDefaultExpireAfterAccess(DEFAULT_EXPIRE_AFTER_ACCESS);
        cf.setDefaultExpireAfterWrite(DEFAULT_EXPIRE_AFTER_WRITE);
        cf.setRedisHostsAndPorts(
                "localhost:7000,localhost:7001,localhost:7002,localhost:7003,localhost:7004,localhost:7005");

        return cf;
    }

}
