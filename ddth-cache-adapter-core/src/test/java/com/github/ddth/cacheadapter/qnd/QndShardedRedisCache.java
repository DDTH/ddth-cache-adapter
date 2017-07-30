package com.github.ddth.cacheadapter.qnd;

import java.util.Random;

import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.redis.ShardedRedisCacheFactory;

import redis.clients.jedis.ShardedJedis;

public class QndShardedRedisCache {

    private static class MyRedisCacheFactory extends ShardedRedisCacheFactory {
        public MyRedisCacheFactory init() {
            super.init();

            try (ShardedJedis jedis = getJedisPool().getResource()) {
                jedis.getAllShards().forEach(j -> j.flushAll());
            }

            return this;
        }
    }

    public static void main(String[] args) throws Exception {
        Random RAND = new Random(System.currentTimeMillis());
        try (ShardedRedisCacheFactory cf = new MyRedisCacheFactory()) {
            cf.setRedisHostsAndPorts("localhost:7379,localhost:7380").init();

            ICache cache = cf.createCache("demo");

            for (int i = 0; i < 10; i++) {
                cache.set("KEY" + i, i, -1, 10);
            }

            for (long t = System.currentTimeMillis(); t + 10000 >= System.currentTimeMillis();) {
                String KEY = "KEY" + RAND.nextInt(10);
                Object value = cache.get(KEY);
                System.out.println(value + "\t" + System.currentTimeMillis());
                Thread.sleep(RAND.nextInt(1000));
            }
        }
    }
}
