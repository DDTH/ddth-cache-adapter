package com.github.ddth.cacheadapter.qnd;

import com.github.ddth.cacheadapter.CacheException;
import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.cacheimpl.redis.ClusteredRedisCacheFactory;
import com.github.ddth.cacheadapter.cacheimpl.redis.BaseRedisCache.KeyMode;
import com.github.ddth.cacheadapter.test.TestValue;

public class QndClusteredRedisCache {

    public static void main(String[] args) throws Exception {
        try (ClusteredRedisCacheFactory cf = new ClusteredRedisCacheFactory()) {
            cf.setRedisHostsAndPorts("10.100.174.51:73791,10.100.174.52:73791,10.100.174.53:73791");
            cf.setRedisPassword("r3d1sp2ssw0rd");
            cf.setKeyMode(KeyMode.MONOPOLISTIC);
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

        // try (ClusteredRedisCacheFactory cf = new
        // ClusteredRedisCacheFactory()) {
        // cf.setRedisHostsAndPorts("10.100.174.51:7379,10.100.174.52:7379,10.100.174.53:7379");
        // cf.setRedisPassword("r3d1sp2ssw0rd");
        // cf.init();
        //
        // Random RAND = new Random(System.currentTimeMillis());
        //
        // ICache cache = cf.createCache("demo");
        // while (true) {
        // try {
        // String keySet = "key-" + RAND.nextInt(10);
        // cache.set(keySet, keySet);
        // Thread.sleep(RAND.nextInt(1981));
        //
        // String keyGet = "key-" + RAND.nextInt(10);
        // System.out.println("GET " + keyGet + ": " + cache.get(keyGet));
        // Thread.sleep(RAND.nextInt(1981));
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        // }
    }

}
