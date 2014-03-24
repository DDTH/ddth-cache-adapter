package com.github.ddth.cacheadapter.redis;

import com.github.ddth.cacheadapter.AbstractCache;
import com.github.ddth.cacheadapter.AbstractCacheFactory;
import com.github.ddth.cacheadapter.ICacheFactory;
import com.github.ddth.cacheadapter.ICacheLoader;
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
            long expireAfterAccess, ICacheLoader cacheLoader) {

        RedisCache cache = new RedisCache(this, name);
        cache.setCapacity(capacity > 0 ? capacity : getDefaultCacheCapacity());
        cache.setExpireAfterAccess(expireAfterAccess);
        cache.setExpireAfterWrite(expireAfterWrite);

        cache.setRedisHost(redisHost).setRedisPort(redisPort).setRedisUsername(redisUsername)
                .setRedisPassword(redisPassword).setPoolConfig(poolConfig);

        cache.init();
        return cache;
    }

}
