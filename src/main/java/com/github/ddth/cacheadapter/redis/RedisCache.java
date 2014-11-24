package com.github.ddth.cacheadapter.redis;

import com.github.ddth.cacheadapter.AbstractCache;
import com.github.ddth.cacheadapter.AbstractCacheFactory;
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

    private String redisHost = "localhost", redisUsername, redisPassword;
    private int redisPort = IRedisClient.DEFAULT_REDIS_PORT;
    private PoolConfig poolConfig;
    private long timeToLiveSeconds = -1;
    private boolean compactMode = false;

    public RedisCache() {
    }

    public RedisCache(String name, AbstractCacheFactory cacheFactory) {
        super(name, cacheFactory);
    }

    public RedisCache(String name, AbstractCacheFactory cacheFactory, long capacity) {
        super(name, cacheFactory, capacity);
    }

    public RedisCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess);
    }

    public RedisCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess, ICacheLoader cacheLoader) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess, cacheLoader);
    }

    protected RedisClientFactory getRedisClientFactory() {
        RedisCacheFactory rfc = (RedisCacheFactory) getCacheFactory();
        return rfc.getRedisClientFactory();
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
    public RedisCache setCompactMode(boolean compactMode) {
        this.compactMode = compactMode;
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
     * Generates "flat" cachekey for non-compact mode.
     * 
     * @param key
     * @return
     * @since 0.1.1
     */
    protected String genCacheKeyNonCompact(String key) {
        return getName() + "-" + key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize() {
        IRedisClient redisClient = getRedisClient();
        try {
            return redisClient != null && compactMode ? redisClient.hashSize(getName()) : -1;
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
                long ttl = 0;
                if (!(entry instanceof CacheEntry)) {
                    CacheEntry ce = new CacheEntry(key, entry, expireAfterWrite, expireAfterAccess);
                    entry = ce;
                    ttl = expireAfterAccess > 0 ? expireAfterAccess
                            : (expireAfterWrite > 0 ? expireAfterWrite
                                    : (timeToLiveSeconds > 0 ? timeToLiveSeconds : 0));
                } else {
                    CacheEntry ce = (CacheEntry) entry;
                    ttl = ce.touch() ? ce.getExpireAfterAccess() : IRedisClient.TTL_NO_CHANGE;
                }
                byte[] data = serializeObject(entry);

                if (compactMode) {
                    redisClient.hashSet(getName(), key, data, (int) ttl);
                } else {
                    redisClient.set(genCacheKeyNonCompact(key), data, (int) ttl);
                }
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
                if (compactMode) {
                    redisClient.hashDelete(getName(), key);
                } else {
                    redisClient.delete(genCacheKeyNonCompact(key));
                }
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
        if (redisClient != null && compactMode) {
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
                if (compactMode) {
                    return redisClient.hashGet(getName(), key) != null;
                } else {
                    return redisClient.get(genCacheKeyNonCompact(key)) != null;
                }
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
                byte[] obj = compactMode ? redisClient.hashGetAsBinary(getName(), key)
                        : redisClient.getAsBinary(genCacheKeyNonCompact(key));
                if (obj != null) {
                    long expireAfterAccess = getExpireAfterAccess();
                    if (expireAfterAccess > 0) {
                        redisClient.expire(compactMode ? getName() : genCacheKeyNonCompact(key),
                                (int) expireAfterAccess);
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
