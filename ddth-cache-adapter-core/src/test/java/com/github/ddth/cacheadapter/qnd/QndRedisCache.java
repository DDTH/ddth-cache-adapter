package com.github.ddth.cacheadapter.qnd;

import com.github.ddth.cacheadapter.CacheException;
import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.cacheimpl.redis.RedisCacheFactory;
import com.github.ddth.cacheadapter.cacheimpl.redis.BaseRedisCache.KeyMode;
import com.github.ddth.cacheadapter.test.TestValue;

public class QndRedisCache {

    // private static class MyRedisCacheFactory extends RedisCacheFactory {
    // public MyRedisCacheFactory init() {
    // super.init();
    //
    // try (Jedis jedis = getJedisPool().getResource()) {
    // jedis.flushAll();
    // }
    //
    // return this;
    // }
    // }

    public static void main(String[] args) throws Exception {
        try (RedisCacheFactory cf = new RedisCacheFactory()) {
            cf.setRedisHostAndPort("localhost:63791").setKeyMode(KeyMode.HASH).init();

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

        // Random RAND = new Random(System.currentTimeMillis());
        // try (RedisCacheFactory cf = new MyRedisCacheFactory()) {
        // cf.setRedisHostAndPort("localhost:6379").init();
        //
        // ICache cache = cf.createCache("demo");
        // cache.set("KEY", "VALUE", -1, 10);
        // for (long t = System.currentTimeMillis(); t + 10000 >=
        // System.currentTimeMillis();) {
        // Object value = cache.get("KEY");
        // System.out.println(value + "\t" + System.currentTimeMillis());
        // Thread.sleep(RAND.nextInt(1000));
        // }
        // }
    }
}
