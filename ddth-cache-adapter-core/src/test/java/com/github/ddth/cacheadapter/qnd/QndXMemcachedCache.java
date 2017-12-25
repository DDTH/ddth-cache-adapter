package com.github.ddth.cacheadapter.qnd;

import com.github.ddth.cacheadapter.CacheException;
import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.cacheimpl.memcached.XMemcachedCacheFactory;
import com.github.ddth.cacheadapter.cacheimpl.memcached.XMemcachedCache.KeyMode;
import com.github.ddth.cacheadapter.test.TestValue;

public class QndXMemcachedCache {
    public static void main(String[] args) {
        try (XMemcachedCacheFactory cf = new XMemcachedCacheFactory()) {
            cf.setMemcachedHostsAndPorts("localhost:11211").setKeyMode(KeyMode.MONOPOLISTIC).init();

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
