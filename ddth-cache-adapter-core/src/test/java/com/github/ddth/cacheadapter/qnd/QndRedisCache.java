package com.github.ddth.cacheadapter.qnd;

import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.redis.RedisCacheFactory;

public class QndRedisCache {
    public static void main(String[] args) {
        try (RedisCacheFactory cf = new RedisCacheFactory()) {
            cf.setRedisHostAndPort("localhost:6379");

            ICache cache = cf.createCache("demo");
            TestValue.Value v1 = new TestValue.Value();
            cache.set("key", v1);

            Object v = cache.get("key");
            System.out.println(v);
            System.out.println(v1.equals(v));
        }
    }
}
