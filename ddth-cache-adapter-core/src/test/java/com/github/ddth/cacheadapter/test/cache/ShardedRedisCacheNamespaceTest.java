package com.github.ddth.cacheadapter.test.cache;

import org.junit.Before;

import com.github.ddth.cacheadapter.redis.BaseRedisCache.KeyMode;
import com.github.ddth.cacheadapter.redis.ShardedRedisCacheFactory;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.2
 */
public class ShardedRedisCacheNamespaceTest extends BaseShardedRedisCacheTest {

    public ShardedRedisCacheNamespaceTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(ShardedRedisCacheNamespaceTest.class);
    }

    @Before
    public void setUp() {
        ShardedRedisCacheFactory cf = buildRedisCacheFactory();
        cf.setKeyMode(KeyMode.NAMESPACE);
        cf.init();
        cacheFactory = cf;
    }

}
