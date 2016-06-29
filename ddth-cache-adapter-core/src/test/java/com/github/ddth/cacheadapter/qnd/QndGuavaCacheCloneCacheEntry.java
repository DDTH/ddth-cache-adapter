package com.github.ddth.cacheadapter.qnd;

import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.ICacheFactory;
import com.github.ddth.cacheadapter.guava.GuavaCacheFactory;

public class QndGuavaCacheCloneCacheEntry {

    private static class ValueClass1 {
        public String toString() {
            return this.getClass().getName();
        }
    }

    private static class ValueClass2 implements Cloneable {
        public String toString() {
            return this.getClass().getName();
        }
    }

    public static void main(String[] args) {
        {
            ICacheFactory factory = new GuavaCacheFactory().setDefaultCacheCapacity(1000)
                    .setDefaultExpireAfterAccess(3600).init();
            ICache cache = factory.createCache("cache");
            String key = "key";
            Object entry = new ValueClass1();
            cache.set(key, entry);

            Object _entry = cache.get(key);
            System.out.println(_entry == entry);
        }
        {
            ICacheFactory factory = new GuavaCacheFactory().setCloneCacheEntries(true)
                    .setDefaultCacheCapacity(1000).setDefaultExpireAfterAccess(3600).init();
            ICache cache = factory.createCache("cache");
            String key = "key";
            Object entry = new ValueClass2();
            cache.set(key, entry);

            Object _entry = cache.get(key);
            System.out.println(_entry == entry);
        }
        {
            ICacheFactory factory = new GuavaCacheFactory().setDefaultCacheCapacity(1000)
                    .setDefaultExpireAfterAccess(3600).init();
            ICache cache = factory.createCache("cache");
            String key = "key";
            Object entry = new ValueClass1();
            cache.set(key, new CacheEntry(key, entry));

            Object _entry = cache.get(key);
            System.out.println(_entry == entry);
        }
        {
            ICacheFactory factory = new GuavaCacheFactory().setCloneCacheEntries(true)
                    .setDefaultCacheCapacity(1000).setDefaultExpireAfterAccess(3600).init();
            ICache cache = factory.createCache("cache");
            String key = "key";
            Object entry = new ValueClass2();
            cache.set(key, new CacheEntry(key, entry));

            Object _entry = cache.get(key);
            System.out.println(_entry == entry);
        }
    }

}
