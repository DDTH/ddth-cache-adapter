package com.github.ddth.cacheadapter.test.guava;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;

import com.github.ddth.cacheadapter.cacheimpl.guava.GuavaCacheFactory;
import com.github.ddth.cacheadapter.test.cache.BaseCacheTCase;

/**
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.2
 */
public class GuavaCacheTCase extends BaseCacheTCase {

    @Before
    public void setUp() {
        Map<String, Properties> cacheProperties = new HashMap<>();

        GuavaCacheFactory cf = new GuavaCacheFactory();
        cf.setCacheProperties(cacheProperties);
        cf.setDefaultCacheCapacity(DEFAULT_CACHE_CAPACITY);
        cf.setDefaultExpireAfterAccess(DEFAULT_EXPIRE_AFTER_ACCESS);
        cf.setDefaultExpireAfterWrite(DEFAULT_EXPIRE_AFTER_WRITE);
        cf.init();
        cacheFactory = cf;
    }

}
