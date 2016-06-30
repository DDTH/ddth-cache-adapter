package com.github.ddth.cacheadapter.qnd;

import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.ICacheFactory;
import com.github.ddth.cacheadapter.guava.GuavaCacheFactory;

public class QndGuavaCacheCloneCacheEntry {

    private static class ValueClass {
        public int value = 0;
    }

    private static class ValueClass1 extends ValueClass {
    }

    private static class ValueClass2 extends ValueClass implements Cloneable {
    }

    public static void main(String[] args) {
        {
            ICacheFactory factory = new GuavaCacheFactory().setDefaultCacheCapacity(1000)
                    .setDefaultExpireAfterAccess(3600).init();
            ICache cache = factory.createCache("cache");
            String key = "key";
            ValueClass entry = new ValueClass1();
            cache.set(key, entry);

            Object _entry = cache.get(key);
            entry.value++;
            System.out.println(_entry == entry);
        }
        {
            ICacheFactory factory = new GuavaCacheFactory().setCloneCacheEntries(true)
                    .setDefaultCacheCapacity(1000).setDefaultExpireAfterAccess(3600).init();
            ICache cache = factory.createCache("cache");
            String key = "key";
            ValueClass entry = new ValueClass2();
            cache.set(key, entry);

            Object _entry = cache.get(key);
            entry.value++;
            System.out.println(_entry == entry);
        }
        {
            ICacheFactory factory = new GuavaCacheFactory().setDefaultCacheCapacity(1000)
                    .setDefaultExpireAfterAccess(3600).init();
            ICache cache = factory.createCache("cache");
            String key = "key";
            ValueClass entry = new ValueClass1();
            cache.set(key, new CacheEntry(key, entry));

            Object _entry = cache.get(key);
            entry.value++;
            System.out.println(_entry == entry);
        }
        {
            ICacheFactory factory = new GuavaCacheFactory().setCloneCacheEntries(true)
                    .setDefaultCacheCapacity(1000).setDefaultExpireAfterAccess(3600).init();
            ICache cache = factory.createCache("cache");
            String key = "key";
            ValueClass entry = new ValueClass2();
            cache.set(key, new CacheEntry(key, entry));

            Object _entry = cache.get(key);
            entry.value++;
            System.out.println(_entry == entry);
        }
    }

}
