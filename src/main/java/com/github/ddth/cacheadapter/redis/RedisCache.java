package com.github.ddth.cacheadapter.redis;

import com.github.ddth.cacheadapter.AbstractCache;
import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.ICacheLoader;
import com.github.ddth.commons.utils.SerializationUtils;
import com.github.ddth.redis.IRedisClient;
import com.github.ddth.redis.PoolConfig;
import com.github.ddth.redis.RedisClientFactory;

/**
 * <a href="https://github.com/DDTH/ddth-redis">Redis</a> implementation of
 * {@link ICache}.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class RedisCache extends AbstractCache {

    private RedisCacheFactory redisCacheFactory;
    private String redisHost = "localhost", redisUsername, redisPassword;
    private int redisPort = IRedisClient.DEFAULT_REDIS_PORT;
    private PoolConfig poolConfig;
    private long timeToLiveSeconds = -1;

    public RedisCache(RedisCacheFactory redisCacheFactory) {
        this.redisCacheFactory = redisCacheFactory;
    }

    public RedisCache(RedisCacheFactory redisCacheFactory, String name) {
        super(name);
        this.redisCacheFactory = redisCacheFactory;
    }

    public RedisCache(RedisCacheFactory redisCacheFactory, String name, long capacity) {
        super(name, capacity);
        this.redisCacheFactory = redisCacheFactory;
    }

    public RedisCache(RedisCacheFactory redisCacheFactory, String name, long capacity,
            long expireAfterWrite, long expireAfterAccess) {
        super(name, capacity, expireAfterWrite, expireAfterAccess);
        this.redisCacheFactory = redisCacheFactory;
    }

    public RedisCache(RedisCacheFactory redisCacheFactory, String name, long capacity,
            long expireAfterWrite, long expireAfterAccess, ICacheLoader cacheLoader) {
        super(name, capacity, expireAfterWrite, expireAfterAccess, cacheLoader);
        this.redisCacheFactory = redisCacheFactory;
    }

    protected RedisClientFactory getRedisClientFactory() {
        return redisCacheFactory.getRedisClientFactory();
    }

    public String getRedisHost() {
        return redisHost;
    }

    public RedisCache setRedisHost(String redisHost) {
        this.redisHost = redisHost;
        return this;
    }

    public String getRedisUsername() {
        return redisUsername;
    }

    public RedisCache setRedisUsername(String redisUsername) {
        this.redisUsername = redisUsername;
        return this;
    }

    public String getRedisPassword() {
        return redisPassword;
    }

    public RedisCache setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
        return this;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public RedisCache setRedisPort(int redisPort) {
        this.redisPort = redisPort;
        return this;
    }

    public PoolConfig getPoolConfig() {
        return poolConfig;
    }

    public RedisCache setPoolConfig(PoolConfig poolConfig) {
        this.poolConfig = poolConfig;
        return this;
    }

    /**
     * Serializes an object to byte array.
     * 
     * @param obj
     * @return
     */
    protected byte[] serializeObject(Object obj) {
        return SerializationUtils.toByteArray(obj);
    }

    /**
     * De-serializes object from byte array.
     * 
     * @param byteArr
     * @return
     */
    protected Object deserializeObject(byte[] byteArr) {
        return SerializationUtils.fromByteArray(byteArr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        super.init();
        long expireAfterWrite = getExpireAfterWrite();
        long expireAfterAccess = getExpireAfterAccess();
        if (expireAfterAccess > 0 || expireAfterWrite > 0) {
            timeToLiveSeconds = expireAfterAccess > 0 ? expireAfterAccess : expireAfterWrite;
        } else {
            timeToLiveSeconds = -1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        // EMPTY
    }

    private IRedisClient getRedisClient() {
        RedisClientFactory redisClientFactory = getRedisClientFactory();
        return redisClientFactory != null ? redisClientFactory.getRedisClient(redisHost, redisPort,
                redisUsername, redisPassword, poolConfig) : null;
    }

    private void returnRedisClient(IRedisClient redisClient) {
        RedisClientFactory redisClientFactory = getRedisClientFactory();
        if (redisClient != null && redisClientFactory != null) {
            redisClientFactory.returnRedisClient(redisClient);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize() {
        IRedisClient redisClient = getRedisClient();
        try {
            return redisClient != null ? redisClient.hashSize(getName()) : -1;
        } finally {
            returnRedisClient(redisClient);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String key, Object entry) {
        if (entry instanceof CacheEntry) {
            CacheEntry ce = (CacheEntry) entry;
            set(key, ce, ce.getExpireAfterWrite(), ce.getExpireAfterAccess());
        } else {
            set(key, entry, getExpireAfterWrite(), getExpireAfterAccess());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String key, Object entry, long expireAfterWrite, long expireAfterAccess) {
        IRedisClient redisClient = getRedisClient();
        if (redisClient != null) {
            try {
                long ttl = expireAfterAccess > 0 ? expireAfterAccess
                        : (expireAfterWrite > 0 ? expireAfterWrite : timeToLiveSeconds);
                redisClient.hashSet(getName(), key, serializeObject(entry), (int) ttl);
            } finally {
                returnRedisClient(redisClient);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String key) {
        IRedisClient redisClient = getRedisClient();
        if (redisClient != null) {
            try {
                redisClient.hashDelete(getName(), key);
            } finally {
                returnRedisClient(redisClient);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        IRedisClient redisClient = getRedisClient();
        if (redisClient != null) {
            try {
                redisClient.delete(getName());
            } finally {
                returnRedisClient(redisClient);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String key) {
        IRedisClient redisClient = getRedisClient();
        if (redisClient != null) {
            try {
                return redisClient.hashGet(getName(), key) != null;
            } finally {
                returnRedisClient(redisClient);
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object internalGet(String key) {
        IRedisClient redisClient = getRedisClient();
        if (redisClient != null) {
            try {
                byte[] obj = redisClient.hashGetAsBinary(getName(), key);
                if (obj != null) {
                    long expireAfterAccess = getExpireAfterAccess();
                    if (expireAfterAccess > 0) {
                        redisClient.expire(getName(), (int) expireAfterAccess);
                    }
                    return deserializeObject(obj);
                } else {
                    return null;
                }
            } finally {
                returnRedisClient(redisClient);
            }
        } else {
            return null;
        }
    }

}
