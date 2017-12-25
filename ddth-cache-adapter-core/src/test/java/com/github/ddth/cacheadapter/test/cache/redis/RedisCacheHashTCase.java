package com.github.ddth.cacheadapter.test.cache.redis;

import org.junit.Before;

import com.github.ddth.cacheadapter.cacheimpl.redis.RedisCacheFactory;
import com.github.ddth.cacheadapter.cacheimpl.redis.BaseRedisCache.KeyMode;

/**
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.2
 */
public class RedisCacheHashTCase extends BaseRedisCacheTCase {

    @Before
    public void setUp() {
        RedisCacheFactory cf = buildRedisCacheFactory();
        cf.setKeyMode(KeyMode.HASH);
        cf.init();
        cacheFactory = cf;
    }

}
