package com.github.ddth.cacheadapter.test.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import com.github.ddth.cacheadapter.memcached.XMemcachedCacheFactory;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.rubyeye.xmemcached.exception.MemcachedException;

/**
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.2
 */
public abstract class BaseMemcachedCacheTest extends BaseCacheTest {

    public BaseMemcachedCacheTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(BaseMemcachedCacheTest.class);
    }

    protected static class MyMemcachedCacheFactory extends XMemcachedCacheFactory {
        public XMemcachedCacheFactory init() {
            super.init();
            try {
                getMemcachedClient().flushAll();
            } catch (TimeoutException | InterruptedException | MemcachedException e) {
                e.printStackTrace();
            }
            return this;
        }
    }

    protected XMemcachedCacheFactory buildMemcachedCacheFactory() {
        Map<String, Properties> cacheProperties = new HashMap<>();

        XMemcachedCacheFactory cf = new MyMemcachedCacheFactory();
        cf.setCacheProperties(cacheProperties);
        cf.setDefaultCacheCapacity(DEFAULT_CACHE_CAPACITY);
        cf.setDefaultExpireAfterAccess(DEFAULT_EXPIRE_AFTER_ACCESS);
        cf.setDefaultExpireAfterWrite(DEFAULT_EXPIRE_AFTER_WRITE);
        cf.setMemcachedHostsAndPorts("localhost:11211");

        return cf;
    }

}
