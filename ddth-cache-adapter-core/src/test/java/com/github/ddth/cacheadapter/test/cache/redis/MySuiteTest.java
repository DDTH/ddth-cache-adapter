package com.github.ddth.cacheadapter.test.cache.redis;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({ RedisCacheHashTCase.class, RedisCacheMonolisticTCase.class,
        RedisCacheNamespaceTCase.class })

public class MySuiteTest {

}
