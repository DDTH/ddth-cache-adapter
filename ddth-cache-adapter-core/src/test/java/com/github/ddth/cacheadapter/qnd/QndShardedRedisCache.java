package com.github.ddth.cacheadapter.qnd;

import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.redis.ShardedRedisCacheFactory;

public class QndShardedRedisCache {

    public static void main(String[] args) {
        try (ShardedRedisCacheFactory cf = new ShardedRedisCacheFactory()) {
            cf.setRedisHostsAndPorts("localhost:6379,localhost:6380");
            cf.init();

            ICache cache = cf.createCache("demo");
            for (int i = 0; i < 4; i++) {
                cache.set("key-" + i, String.valueOf(i));
            }

            for (int i = 2; i < 6; i++) {
                String key = "key-" + i;
                System.out.println(cache.get(key));
            }
        }
    }

}
