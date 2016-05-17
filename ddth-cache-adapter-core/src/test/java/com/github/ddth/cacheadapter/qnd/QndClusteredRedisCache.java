package com.github.ddth.cacheadapter.qnd;

import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.redis.ClusteredRedisCacheFactory;

public class QndClusteredRedisCache {

    public static void main(String[] args) {
        ClusteredRedisCacheFactory cf = new ClusteredRedisCacheFactory();
        try {
            cf.setRedisHostsAndPorts("localhost:30001,localhost:30002,localhost:30003");
            cf.init();

            ICache cache = cf.createCache("demo");
            for (int i = 0; i < 4; i++) {
                cache.set("key-" + i, String.valueOf(i));
            }

            for (int i = 2; i < 6; i++) {
                String key = "key-" + i;
                System.out.println(cache.get(key));
            }
        } finally {
            cf.destroy();
        }
    }

}
