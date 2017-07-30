package com.github.ddth.cacheadapter.test.cache.shardedredis;

import org.junit.Before;

import com.github.ddth.cacheadapter.redis.BaseRedisCache.KeyMode;
import com.github.ddth.cacheadapter.redis.ShardedRedisCacheFactory;

/**
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.2
 */
public class ShardedRedisCacheNamespaceTCase extends BaseShardedRedisCacheTCase {

    @Before
    public void setUp() {
        ShardedRedisCacheFactory cf = buildRedisCacheFactory();
        cf.setKeyMode(KeyMode.NAMESPACE);
        cf.init();
        cacheFactory = cf;
    }

}
