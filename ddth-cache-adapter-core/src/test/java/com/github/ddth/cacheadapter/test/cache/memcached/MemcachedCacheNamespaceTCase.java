package com.github.ddth.cacheadapter.test.cache.memcached;

import org.junit.Before;

import com.github.ddth.cacheadapter.cacheimpl.memcached.XMemcachedCacheFactory;
import com.github.ddth.cacheadapter.cacheimpl.memcached.XMemcachedCache.KeyMode;

/**
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.2
 */
public class MemcachedCacheNamespaceTCase extends BaseMemcachedCacheTCase {

    @Before
    public void setUp() {
        XMemcachedCacheFactory cf = buildMemcachedCacheFactory();
        cf.setKeyMode(KeyMode.NAMESPACE);
        cf.init();
        cacheFactory = cf;
    }

}
