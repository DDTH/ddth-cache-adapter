package com.github.ddth.cacheadapter.test.cache;

import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.ICacheFactory;
import com.github.ddth.cacheadapter.test.TestValue;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Base class for cache-entry serializer test cases.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 */
public abstract class BaseCacheTest extends TestCase {

    public BaseCacheTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(BaseCacheTest.class);
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
        ICache cache = cacheFactory.createCache("cache1default");
        assertNotNull(cache);

        TestValue.Value testValue = new TestValue.Value();
        Object[] values = { 1, 2L, 1.1, 2.2d, true, "demo", 'c', testValue };

        for (int i = 0; i < values.length; i++) {
            String key = "key" + i;
            cache.set(key, values[i]);
        }

        for (int i = 0; i < values.length; i++) {
            String key = "key" + i;
            Object value = cache.get(key);
            assertEquals(values[i], value);
        }
    }

}
