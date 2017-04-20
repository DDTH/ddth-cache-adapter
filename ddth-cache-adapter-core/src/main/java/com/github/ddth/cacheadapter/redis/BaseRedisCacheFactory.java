package com.github.ddth.cacheadapter.redis;

import com.github.ddth.cacheadapter.AbstractSerializingCacheFactory;
import com.github.ddth.cacheadapter.ICacheFactory;
import com.github.ddth.cacheadapter.redis.BaseRedisCache.KeyMode;

/**
 * Base class for <a href="http://redis.io">Redis</a>-based implementations of
 * {@link ICacheFactory}.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.0
 */
public abstract class BaseRedisCacheFactory extends AbstractSerializingCacheFactory {

    /**
     * @see KeyMode
     */
    protected KeyMode keyMode = KeyMode.NAMESPACE;

    /**
     * Flag to mark if the Redis resource (e.g. Redis client pool) is created and handled by the
     * factory.
     */
    protected boolean myOwnRedis = false;

    /**
     * Should the factory build a global Redis resource (e.g. Redis client pool) to be shared by all
     * Redis caches created by this factory. Default is {@code true}.
     */
    private boolean buildGlobalRedis = true;

    private String redisPassword;

    public String getRedisPassword() {
        return redisPassword;
    }

    public BaseRedisCacheFactory setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
        return this;
    }

    /**
     * See {@link KeyMode}.
     * 
     * @return
     */
    public KeyMode getKeyMode() {
        return keyMode;
    }

    /**
     * See {@link KeyMode}.
     * 
     * @param keyMode
     * @return
     */
    public BaseRedisCacheFactory setKeyMode(KeyMode keyMode) {
        this.keyMode = keyMode;
        return this;
    }

    /**
     * Should the factory build a global Redis resource (e.g. Redis client pool) to be shared by all
     * Redis caches created by this factory.
     * 
     * @return
     */
    public boolean isBuildGlobalRedis() {
        return getBuildGlobalRedis();
    }

    /**
     * Should the factory build a global Redis resource (e.g. Redis client pool) to be shared by all
     * Redis caches created by this factory.
     * 
     * @return
     */
    public boolean getBuildGlobalRedis() {
        return buildGlobalRedis;
    }

    /**
     * Should the factory build a global Redis resource (e.g. Redis client pool) to be shared by all
     * Redis caches created by this factory.
     * 
     * @param buildGlobalRedis
     * @return
     */
    public BaseRedisCacheFactory setBuildGlobalRedis(boolean buildGlobalRedis) {
        this.buildGlobalRedis = buildGlobalRedis;
        return null;
    }

}
