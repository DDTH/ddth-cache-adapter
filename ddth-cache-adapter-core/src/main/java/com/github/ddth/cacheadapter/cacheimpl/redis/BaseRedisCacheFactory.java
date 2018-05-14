package com.github.ddth.cacheadapter.cacheimpl.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ddth.cacheadapter.AbstractSerializingCacheFactory;
import com.github.ddth.cacheadapter.ICacheFactory;
import com.github.ddth.cacheadapter.cacheimpl.redis.BaseRedisCache.KeyMode;
import com.github.ddth.commons.redis.JedisConnector;

/**
 * Base class for <a href="http://redis.io">Redis</a>-based implementations of
 * {@link ICacheFactory}.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.0
 */
public abstract class BaseRedisCacheFactory extends AbstractSerializingCacheFactory {

    private final Logger LOGGER = LoggerFactory.getLogger(BaseRedisCacheFactory.class);

    /**
     * @see KeyMode
     */
    protected KeyMode keyMode = KeyMode.NAMESPACE;

    /**
     * Flag to mark if the Redis resource (e.g. Redis client pool) is created
     * and handled by the factory.
     */
    protected boolean myOwnRedis = false;

    private String redisPassword;
    private JedisConnector jedisConnector;

    public String getRedisPassword() {
        return redisPassword;
    }

    public BaseRedisCacheFactory setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
        return this;
    }

    /**
     * @return
     * @since 0.6.3
     */
    protected JedisConnector getJedisConnector() {
        return jedisConnector;
    }

    /**
     * @param jedisConnector
     * @return
     * @since 0.6.3
     */
    public BaseRedisCacheFactory setJedisConnector(JedisConnector jedisConnector) {
        return setJedisConnector(jedisConnector, false);
    }

    /**
     * Attach a {@link JedisConnector} to this cache.
     * 
     * @param jedisConnector
     * @param setMyOwnRedis
     *            set {@link #myOwnRedis} to {@code true} or not.
     * @return
     * @since 0.6.3.3
     */
    protected BaseRedisCacheFactory setJedisConnector(JedisConnector jedisConnector,
            boolean setMyOwnRedis) {
        if (myOwnRedis && this.jedisConnector != null) {
            this.jedisConnector.close();
        }
        this.jedisConnector = jedisConnector;
        myOwnRedis = setMyOwnRedis;
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
     * Should the factory build a global Redis resource (e.g. Redis client pool)
     * to be shared by all Redis caches created by this factory.
     * 
     * @return
     * @deprecated deprecated since 0.6.3
     */
    public boolean isBuildGlobalRedis() {
        return true;
    }

    /**
     * Should the factory build a global Redis resource (e.g. Redis client pool)
     * to be shared by all Redis caches created by this factory.
     * 
     * @return
     * @deprecated deprecated since 0.6.3
     */
    public boolean getBuildGlobalRedis() {
        return true;
    }

    /**
     * Should the factory build a global Redis resource (e.g. Redis client pool)
     * to be shared by all Redis caches created by this factory.
     * 
     * @param buildGlobalRedis
     * @return
     * @deprecated deprecated since 0.6.3
     */
    public BaseRedisCacheFactory setBuildGlobalRedis(boolean buildGlobalRedis) {
        return null;
    }

    /**
     * @since 0.6.3
     */
    public void destroy() {
        try {
            super.destroy();
        } finally {
            if (jedisConnector != null && myOwnRedis) {
                try {
                    jedisConnector.destroy();
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                } finally {
                    jedisConnector = null;
                }
            }
        }
    }
}
