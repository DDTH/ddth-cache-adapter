package com.github.ddth.cacheadapter.test.cache.redis;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({ RedisCacheHashTCase.class, RedisCacheMonolisticTCase.class,
        RedisCacheNamespaceTCase.class })

/*
 * Use https://github.com/Grokzen/docker-redis-cluster for testing
 */
public class MySuiteTest {

}
