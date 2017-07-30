package com.github.ddth.cacheadapter.test.cache.shardedredis;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({ ShardedRedisCacheHashTCase.class, ShardedRedisCacheMonolisticTCase.class,
        ShardedRedisCacheNamespaceTCase.class })

public class MySuiteTest {

}
