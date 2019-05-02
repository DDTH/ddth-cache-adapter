package com.github.ddth.cacheadapter.test.cache.clusteredredis;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({ ClusteredRedisCacheHashTCase.class, ClusteredRedisCacheMonolisticTCase.class,
        ClusteredRedisCacheNamespaceTCase.class })

/*
 * Use https://github.com/Grokzen/docker-redis-cluster for testing
 */
public class MySuiteTest {

}
