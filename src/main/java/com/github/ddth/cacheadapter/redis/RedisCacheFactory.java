package com.github.ddth.cacheadapter.redis;

import com.github.ddth.cacheadapter.AbstractCache;
import com.github.ddth.cacheadapter.AbstractCacheFactory;
import com.github.ddth.cacheadapter.ICacheFactory;
import com.github.ddth.redis.IRedisClient;
import com.github.ddth.redis.PoolConfig;
import com.github.ddth.redis.RedisClientFactory;

/**
 * <a href="https://github.com/DDTH/ddth-redis">Redis</a> implementation of
 * {@link ICacheFactory}.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class RedisCacheFactory extends AbstractCacheFactory {

    private boolean myOwnRedisClientFactory = false;
    private RedisClientFactory redisClientFactory;
    private String redisHost = "localhost", redisUsername, redisPassword;
    private int redisPort = IRedisClient.DEFAULT_REDIS_PORT;
    private PoolConfig poolConfig;
    private boolean compactMode = false;

    public RedisClientFactory getRedisClientFactory() {
        return redisClientFactory;
    }

    public RedisCacheFactory setRedisClientFactory(RedisClientFactory redisClientFactory) {
        if (myOwnRedisClientFactory && this.redisClientFactory != null
                && this.redisClientFactory != redisClientFactory) {
            this.redisClientFactory.destroy();
        }
        this.redisClientFactory = redisClientFactory;
        myOwnRedisClientFactory = false;
        return this;
    }

    public String getRedisHost() {
        return redisHost;
    }

    public RedisCacheFactory setRedisHost(String redisHost) {
        this.redisHost = redisHost;
        return this;
    }

    protected String getRedisUsername() {
        return redisUsername;
    }

    public RedisCacheFactory setRedisUsername(String redisUsername) {
        this.redisUsername = redisUsername;
        return this;
    }

    protected String getRedisPassword() {
        return redisPassword;
    }

    public RedisCacheFactory setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
        return this;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public RedisCacheFactory setRedisPort(int redisPort) {
        this.redisPort = redisPort;
        return this;
    }

    protected PoolConfig getPoolConfig() {
        return poolConfig;
    }

    public RedisCacheFactory setPoolConfig(PoolConfig poolConfig) {
        this.poolConfig = poolConfig;
        return this;
    }

    /**
     * Is compact mode on or off?
     * 
     * <p>
     * Compact mode: each cache is a Redis hash; cache entries are stored within
     * the hash. When compact mode is off, each cache entry is prefixed by
     * cache-name and store to Redis' top-level key:value storage (default:
     * compact-mode=off).
     * </p>
     * 
     * @return
     * @since 0.1.1
     */
    public boolean isCompactMode() {
        return compactMode;
    }

    /**
     * Is compact mode on or off?
     * 
     * <p>
     * Compact mode: each cache is a Redis hash; cache entries are stored within
     * the hash. When compact mode is off, each cache entry is prefixed by
     * cache-name and store to Redis' top-level key:value storage (default:
     * compact-mode=off).
     * </p>
     * 
     * @return
     * @since 0.1.1
     */
    public boolean getCompactMode() {
        return compactMode;
    }

    /**
     * Sets compact mode on/off.
     * 
     * <p>
     * Compact mode: each cache is a Redis hash; cache entries are stored within
     * the hash. When compact mode is off, each cache entry is prefixed by
     * cache-name and store to Redis' top-level key:value storage (default:
     * compact-mode=off).
     * </p>
     * 
     * @param compactMode
     * @return
     * @since 0.1.1
     */
    public RedisCacheFactory setCompactMode(boolean compactMode) {
        this.compactMode = compactMode;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RedisCacheFactory init() {
        super.init();
        if (redisClientFactory == null) {
            RedisClientFactory factory = RedisClientFactory.newFactory();
            redisClientFactory = factory;
            myOwnRedisClientFactory = true;
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        try {
            if (myOwnRedisClientFactory && redisClientFactory != null) {
                redisClientFactory.destroy();
            }
        } finally {
            super.destroy();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractCache createCacheInternal(String name, long capacity, long expireAfterWrite,
            long expireAfterAccess) {
        RedisCache cache = new RedisCache();
        cache.setName(name).setCapacity(capacity).setExpireAfterAccess(expireAfterAccess)
                .setExpireAfterWrite(expireAfterWrite);
        cache.setRedisHost(redisHost).setRedisPort(redisPort).setRedisUsername(redisUsername)
                .setRedisPassword(redisPassword).setPoolConfig(poolConfig);
        return cache;
    }

}
