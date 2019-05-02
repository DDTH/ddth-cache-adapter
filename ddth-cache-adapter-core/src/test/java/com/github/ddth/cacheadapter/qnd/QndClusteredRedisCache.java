package com.github.ddth.cacheadapter.qnd;

import com.github.ddth.cacheadapter.CacheException;
import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.cacheimpl.redis.ClusteredRedisCacheFactory;
import com.github.ddth.cacheadapter.test.TestValue;

public class QndClusteredRedisCache {

    public static void main(String[] args) throws Exception {
        try (ClusteredRedisCacheFactory cf = new ClusteredRedisCacheFactory()) {
            cf.setRedisHostsAndPorts(
                    "localhost:7000,localhost:7001,localhost:7002,localhost:7003,localhost:7004,localhost:7005");
            // cf.setKeyMode(KeyMode.MONOPOLISTIC);
            cf.init();

            ICache cache = cf.createCache("demo");
            TestValue.Value v1 = new TestValue.Value();

            try {
                cache.set("key", v1);
            } catch (CacheException e) {
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }

            try {
                Object v = cache.get("key");
                System.out.println(v);
                System.out.println(v1.equals(v));
            } catch (CacheException e) {
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }
    }
}
