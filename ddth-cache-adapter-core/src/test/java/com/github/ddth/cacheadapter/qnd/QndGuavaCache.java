package com.github.ddth.cacheadapter.qnd;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.github.ddth.cacheadapter.AbstractCacheFactory;
import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.ICacheFactory;
import com.github.ddth.cacheadapter.cacheimpl.guava.GuavaCacheFactory;
import com.github.ddth.cacheadapter.cacheimpl.redis.RedisCacheFactory;

public class QndGuavaCache {

    @SuppressWarnings({ "resource", "unused" })
    public static void main(String[] args) {
        ICacheFactory factory1 = new GuavaCacheFactory().setDefaultCacheCapacity(1000)
                .setDefaultExpireAfterAccess(3600).init();

        ICacheFactory factory2 = new RedisCacheFactory().setRedisHostAndPort("localhost:6379")
                .init();

        Map<String, Properties> cacheProps = new HashMap<String, Properties>();
        Properties propCache1 = new Properties();
        propCache1.put("cache.capacity", 1000);
        propCache1.put("cache.expireAfterWrite", 3600);
        cacheProps.put("cache1", propCache1);
        Properties propCache2 = new Properties();
        propCache1.put("cache.capacity", 10000);
        propCache1.put("cache.expireAfterAccess", 3600);
        cacheProps.put("cache2", propCache1);
        ICacheFactory factory3 = new GuavaCacheFactory().setCacheProperties(cacheProps)
                .setDefaultCacheCapacity(1000).setDefaultExpireAfterAccess(3600).init();

        ICacheFactory factory = new GuavaCacheFactory().init();
        ICache cache1 = factory.createCache("cache1");
        ICache cache2 = factory.createCache("cache2", 1000);
        // ICache cache3 = factory.createCache(name, capacity, expireAfterWrite,
        // expireAfterAccess, cacheLoader)

        ((AbstractCacheFactory) factory).destroy();
    }

}
