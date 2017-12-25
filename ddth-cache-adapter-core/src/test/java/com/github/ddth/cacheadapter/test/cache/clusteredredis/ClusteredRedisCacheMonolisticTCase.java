package com.github.ddth.cacheadapter.test.cache.clusteredredis;

import org.junit.Before;

import com.github.ddth.cacheadapter.cacheimpl.redis.ClusteredRedisCacheFactory;
import com.github.ddth.cacheadapter.cacheimpl.redis.BaseRedisCache.KeyMode;

/**
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.2
 */
public class ClusteredRedisCacheMonolisticTCase extends BaseClusteredRedisCacheTCase {

    @Before
    public void setUp() {
        ClusteredRedisCacheFactory cf = buildRedisCacheFactory();
        cf.setKeyMode(KeyMode.MONOPOLISTIC);
        cf.init();
        cacheFactory = cf;
    }

}
