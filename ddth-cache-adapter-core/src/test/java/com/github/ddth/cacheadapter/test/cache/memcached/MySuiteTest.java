package com.github.ddth.cacheadapter.test.cache.memcached;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({ MemcachedCacheNamespaceTCase.class, MemcachedCacheXNamespaceTCase.class,
        MemcachedCacheMonolisticTCase.class })

public class MySuiteTest {
}
