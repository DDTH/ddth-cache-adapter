package com.github.ddth.cacheadapter.redis;

import com.github.ddth.cacheadapter.AbstractCacheFactory;
import com.github.ddth.cacheadapter.AbstractSerializingCache;
import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.ICacheEntrySerializer;
import com.github.ddth.cacheadapter.ICacheLoader;

/**
 * Base class for <a href="http://redis.io">Redis</a>-based implementations of
 * {@link ICache}.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.0
 */
public abstract class BaseRedisCache extends AbstractSerializingCache {

    /**
     * Defines how cache key/entries are grouped together.
     */
    public enum KeyMode {
        /**
         * Cache entries are grouped into namespaces: cache keys are prefixed
         * with cache's name. So more than one Redis-based cache instances can
         * share one Redis server/cluster.
         */
        NAMESPACE(0),

        /**
         * Assuming the whole Redis server/cluster is dedicated to the Redis-based
         * cache instance. Cache keys are kept as-is.
         */
        MONOPOLISTIC(1),

        /**
         * Cache entries are grouped into namespaces, each namespace is a Redis
         * hash specified by cache's name. More than one Redis-based cache
         * instances can share one Redis server/cluster. Cache keys are
         * kept as-is.
         */
        HASH(2);

        public final int value;

        KeyMode(int value) {
            this.value = value;
        }
    }

    public final static long TTL_NO_CHANGE = 0;
    public final static long TTL_FOREVER = -1;

    /**
     * Flag to mark if the Redis resource (e.g. Redis client pool) is created and handled by the
     * cache instance.
     */
    protected boolean myOwnRedis = true;
    private String redisPassword;
    protected long timeToLiveSeconds = -1;
    protected final KeyMode keyMode;

    public BaseRedisCache(KeyMode keyMode) {
        super();
        this.keyMode = keyMode;
    }

    public BaseRedisCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory, cacheEntrySerializer);
        this.keyMode = keyMode;
    }

    public BaseRedisCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory,
            long capacity, ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory, capacity, cacheEntrySerializer);
        this.keyMode = keyMode;
    }

    public BaseRedisCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory,
            long capacity, long expireAfterWrite, long expireAfterAccess,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess,
                cacheEntrySerializer);
        this.keyMode = keyMode;
    }

    public BaseRedisCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory,
            long capacity, long expireAfterWrite, long expireAfterAccess, ICacheLoader cacheLoader,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess, cacheLoader,
                cacheEntrySerializer);
        this.keyMode = keyMode;
    }

    public BaseRedisCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory,
            long capacity, long expireAfterWrite, long expireAfterAccess,
            ICacheLoader cacheLoader) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess, cacheLoader);
        this.keyMode = keyMode;
    }

    public BaseRedisCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory,
            long capacity, long expireAfterWrite, long expireAfterAccess) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess);
        this.keyMode = keyMode;
    }

    public BaseRedisCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory,
            long capacity) {
        super(name, cacheFactory, capacity);
        this.keyMode = keyMode;
    }

    public BaseRedisCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory) {
        super(name, cacheFactory);
        this.keyMode = keyMode;
    }

    public String getRedisPassword() {
        return redisPassword;
    }

    public BaseRedisCache setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
        return this;
    }

    public KeyMode getKeyMode() {
        return keyMode;
    }

    public long getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    public BaseRedisCache setTimeToLiveSeconds(long timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
        return this;
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
     * Calculates cache key based on the key mode.
     * 
     * <ul>
     * <li>HASH & MONOPOLISTIC mode: return the key as-is</li>
     * <li>NAMESPACE mode: return {@code cache_name:key}</li>
     * </ul>
     * 
     * @param key
     * @return
     */
    protected String calcCacheKey(String key) {
        switch (keyMode) {
        case HASH:
        case MONOPOLISTIC:
            return key;
        case NAMESPACE:
            return getName() + ":" + key;
        default:
            throw new IllegalStateException("Invalid key mode: " + keyMode);
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

}
