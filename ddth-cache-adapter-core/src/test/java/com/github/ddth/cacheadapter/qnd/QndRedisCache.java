package com.github.ddth.cacheadapter.qnd;

import java.util.Random;

import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.redis.RedisCacheFactory;

import redis.clients.jedis.Jedis;

public class QndRedisCache {

    private static class MyRedisCacheFactory extends RedisCacheFactory {
        public MyRedisCacheFactory init() {
            super.init();

            try (Jedis jedis = getJedisPool().getResource()) {
                jedis.flushAll();
            }

            return this;
        }
    }

    public static void main(String[] args) throws Exception {
        Random RAND = new Random(System.currentTimeMillis());
        try (RedisCacheFactory cf = new MyRedisCacheFactory()) {
            cf.setRedisHostAndPort("localhost:6379").init();

            ICache cache = cf.createCache("demo");
            cache.set("KEY", "VALUE", -1, 10);
            for (long t = System.currentTimeMillis(); t + 10000 >= System.currentTimeMillis();) {
                Object value = cache.get("KEY");
                System.out.println(value + "\t" + System.currentTimeMillis());
                Thread.sleep(RAND.nextInt(1000));
            }
        }
    }
}
