package com.github.ddth.cacheadapter.test.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;

import com.github.ddth.cacheadapter.AbstractCacheFactory;
import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.ICacheFactory;
import com.github.ddth.cacheadapter.test.TestValue;

/**
 * Base class for cache-entry serializer test cases.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 */
public abstract class BaseCacheTCase {

    @After
    public void tearDown() {
        ((AbstractCacheFactory) cacheFactory).destroy();
    }

    protected static ICacheFactory cacheFactory;
    protected final static long DEFAULT_CACHE_CAPACITY = 10;
    protected final static long DEFAULT_EXPIRE_AFTER_ACCESS = -1;
    protected final static long DEFAULT_EXPIRE_AFTER_WRITE = -1;

    @org.junit.Test
    public void testGetCache() {
        ICache cache1 = cacheFactory.createCache("cache1default");
        assertNotNull(cache1);
        assertEquals(DEFAULT_CACHE_CAPACITY, cache1.getCapacity());
        assertEquals(DEFAULT_EXPIRE_AFTER_ACCESS, cache1.getExpireAfterAccess());
        assertEquals(DEFAULT_EXPIRE_AFTER_WRITE, cache1.getExpireAfterWrite());

        ICache cache2 = cacheFactory.createCache("cache1default");
        assertNotNull(cache2);
        assertEquals(cache1, cache2);

        ICache cache3 = cacheFactory.createCache("cache1default", 100, 123, 456);
        assertNotNull(cache3);
        assertEquals(cache1, cache3);
        assertEquals(DEFAULT_CACHE_CAPACITY, cache1.getCapacity());
        assertEquals(DEFAULT_EXPIRE_AFTER_ACCESS, cache1.getExpireAfterAccess());
        assertEquals(DEFAULT_EXPIRE_AFTER_WRITE, cache1.getExpireAfterWrite());
    }

    @org.junit.Test
    public void testSetGet() {
        ICache cache = cacheFactory.createCache("cache1default", 100);
        assertNotNull(cache);

        Object[] values = { 1, 2L, 1.1, 2.2d, true, "demo", 'c', new TestValue.Value(),
                new TestValue.AClass(), new TestValue.BClass(), new TestValue.CClass(),
                new TestValue.NestedValue()

        };

        for (int i = 0; i < values.length; i++) {
            String key = "key" + i;
            cache.set(key, values[i]);
            long cacheSize = cache.getSize();
            if (cacheSize >= 0) {
                assertEquals(i + 1, cacheSize);
            } else {
                assertEquals(-1, cacheSize);
            }
        }

        for (int i = 0; i < values.length; i++) {
            String key = "key" + i;
            Object value = cache.get(key);
            assertEquals(values[i], value);
        }
    }

    @org.junit.Test
    public void testMaxCapacity() {
        int maxCapacity = 10;
        int numEntries = 30;

        ICache cache = cacheFactory.createCache("cache1default", maxCapacity);
        assertNotNull(cache);
        if (cache.isCapacitySupported()) {
            for (int i = 0; i < numEntries; i++) {
                cache.set("key" + i, "value" + i);
            }
            assertEquals(maxCapacity, cache.getSize());
        }
    }

    @org.junit.Test
    public void testExpireAfterWrite() throws Exception {
        final long expireAfterWriteSeconds = 3;
        ICache cache = cacheFactory.createCache("cache1default", DEFAULT_CACHE_CAPACITY,
                expireAfterWriteSeconds, -1);
        assertNotNull(cache);

        final String KEY = "KEY";
        final String VALUE = "VALUE";

        cache.set(KEY, VALUE);
        Thread.sleep(1000);
        Object value = cache.get(KEY);
        assertEquals(VALUE, value);

        Thread.sleep(expireAfterWriteSeconds * 1000);
        value = cache.get(KEY);
        assertNull(value);

        long cacheSize = cache.getSize();
        if (cacheSize >= 0) {
            assertEquals(0, cacheSize);
        } else {
            assertEquals(-1, cacheSize);
        }
    }

    @org.junit.Test
    public void testExpireAfterRead() throws Exception {
        final long expireAfterReadSeconds = 2;
        ICache cache = cacheFactory.createCache("cache1default", DEFAULT_CACHE_CAPACITY, -1,
                expireAfterReadSeconds);
        assertNotNull(cache);

        final String KEY = "KEY";
        final String VALUE = "VALUE";

        cache.set(KEY, VALUE);

        for (int i = 0; i < expireAfterReadSeconds + 2; i++) {
            Thread.sleep(1000);
            Object value = cache.get(KEY);
            assertEquals(VALUE, value);
        }

        Thread.sleep(expireAfterReadSeconds * 1000 + 1000);
        Object value = cache.get(KEY);
        assertNull(value);

        long cacheSize = cache.getSize();
        if (cacheSize >= 0) {
            assertEquals(0, cacheSize);
        } else {
            assertEquals(-1, cacheSize);
        }
    }

}
