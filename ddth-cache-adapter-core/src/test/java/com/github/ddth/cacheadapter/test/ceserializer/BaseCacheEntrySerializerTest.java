package com.github.ddth.cacheadapter.test.ceserializer;

import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.ICacheEntrySerializer;
import com.github.ddth.cacheadapter.test.TestValue;

import junit.framework.TestCase;

/**
 * Base class for cache-entry serializer test cases.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 */
public abstract class BaseCacheEntrySerializerTest extends TestCase {

    public BaseCacheEntrySerializerTest(String testName) {
        super(testName);
    }

    protected static ICacheEntrySerializer cacheEntrySerializer;

    @org.junit.Test
    public void testBaseClass() throws Exception {
        CacheEntry ce = new CacheEntry("key", new TestValue.BaseClass());
        byte[] data = cacheEntrySerializer.serialize(ce);
        assertNotNull(data);

        Object ceBack = cacheEntrySerializer.deserialize(data);
        assertFalse(ce == ceBack);
        assertTrue(ce.equals(ceBack));
    }

    @org.junit.Test
    public void testAClass() throws Exception {
        CacheEntry ce = new CacheEntry("key", new TestValue.AClass());
        byte[] data = cacheEntrySerializer.serialize(ce);
        assertNotNull(data);

        Object ceBack = cacheEntrySerializer.deserialize(data);
        assertFalse(ce == ceBack);
        assertTrue(ce.equals(ceBack));
    }

    @org.junit.Test
    public void testBClass() throws Exception {
        TestValue.BClass obj = new TestValue.BClass();
        obj.obj = new TestValue.BaseClass();

        CacheEntry ce = new CacheEntry("key", obj);
        byte[] data = cacheEntrySerializer.serialize(ce);
        assertNotNull(data);

        Object ceBack = cacheEntrySerializer.deserialize(data);
        assertFalse(ce == ceBack);
        assertTrue(ce.equals(ceBack));
    }

    @org.junit.Test
    public void testBClass2() throws Exception {
        TestValue.BClass obj = new TestValue.BClass();
        obj.obj = new TestValue.AClass();

        CacheEntry ce = new CacheEntry("key", obj);
        byte[] data = cacheEntrySerializer.serialize(ce);
        assertNotNull(data);

        Object ceBack = cacheEntrySerializer.deserialize(data);
        assertFalse(ce == ceBack);
        assertTrue(ce.equals(ceBack));
    }
}
