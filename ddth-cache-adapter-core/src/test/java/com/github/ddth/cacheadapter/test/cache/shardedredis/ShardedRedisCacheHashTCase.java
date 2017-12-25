package com.github.ddth.cacheadapter.test.cache.shardedredis;

import org.junit.Before;

import com.github.ddth.cacheadapter.cacheimpl.redis.ShardedRedisCacheFactory;
import com.github.ddth.cacheadapter.cacheimpl.redis.BaseRedisCache.KeyMode;

/**
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.2
 */
public class ShardedRedisCacheHashTCase extends BaseShardedRedisCacheTCase {

    @Before
    public void setUp() {
        ShardedRedisCacheFactory cf = buildRedisCacheFactory();
        cf.setKeyMode(KeyMode.HASH);
        cf.init();
        cacheFactory = cf;
    }

}
