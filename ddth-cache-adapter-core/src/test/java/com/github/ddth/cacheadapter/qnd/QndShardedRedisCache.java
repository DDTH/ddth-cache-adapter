package com.github.ddth.cacheadapter.qnd;

import com.github.ddth.cacheadapter.CacheException;
import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.cacheimpl.redis.ShardedRedisCacheFactory;
import com.github.ddth.cacheadapter.cacheimpl.redis.BaseRedisCache.KeyMode;
import com.github.ddth.cacheadapter.test.TestValue;

public class QndShardedRedisCache {

    // private static class MyRedisCacheFactory extends ShardedRedisCacheFactory
    // {
    // public MyRedisCacheFactory init() {
    // super.init();
    //
    // try (ShardedJedis jedis = getJedisPool().getResource()) {
    // jedis.getAllShards().forEach(j -> j.flushAll());
    // }
    //
    // return this;
    // }
    // }

    public static void main(String[] args) throws Exception {
        try (ShardedRedisCacheFactory cf = new ShardedRedisCacheFactory()) {
            cf.setRedisHostsAndPorts("localhost:63791,localhost:7379").setKeyMode(KeyMode.NAMESPACE)
                    .init();

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
        // try (ShardedRedisCacheFactory cf = new MyRedisCacheFactory()) {
        // cf.setRedisHostsAndPorts("localhost:7379,localhost:7380").init();
        //
        // ICache cache = cf.createCache("demo");
        //
        // for (int i = 0; i < 10; i++) {
        // cache.set("KEY" + i, i, -1, 10);
        // }
        //
        // for (long t = System.currentTimeMillis(); t + 10000 >=
        // System.currentTimeMillis();) {
        // String KEY = "KEY" + RAND.nextInt(10);
        // Object value = cache.get(KEY);
        // System.out.println(value + "\t" + System.currentTimeMillis());
        // Thread.sleep(RAND.nextInt(1000));
        // }
        // }
    }
}
