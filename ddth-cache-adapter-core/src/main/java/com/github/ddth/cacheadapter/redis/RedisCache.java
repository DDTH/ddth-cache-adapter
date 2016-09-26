package com.github.ddth.cacheadapter.redis;

import com.github.ddth.cacheadapter.AbstractCacheFactory;
import com.github.ddth.cacheadapter.AbstractSerializingCache;
import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.ICacheEntrySerializer;
import com.github.ddth.cacheadapter.ICacheLoader;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.SafeEncoder;

/**
 * <a href="http://redis.io">Redis</a> implementation of {@link ICache}.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class RedisCache extends AbstractSerializingCache {

    private JedisPool jedisPool;
    private boolean myOwnJedisPool = true;
    private String redisHostAndPort = "localhost:6379";
    private String redisPassword;

    private long timeToLiveSeconds = -1;
    private boolean compactMode = false;

    public RedisCache() {
        super();
    }

    public RedisCache(String name, AbstractCacheFactory cacheFactory,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory, cacheEntrySerializer);
    }

    public RedisCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory, capacity, cacheEntrySerializer);
    }

    public RedisCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess,
                cacheEntrySerializer);
    }

    public RedisCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess, ICacheLoader cacheLoader,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess, cacheLoader,
                cacheEntrySerializer);
    }

    public RedisCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess, ICacheLoader cacheLoader) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess, cacheLoader);
    }

    public RedisCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess);
    }

    public RedisCache(String name, AbstractCacheFactory cacheFactory, long capacity) {
        super(name, cacheFactory, capacity);
    }

    public RedisCache(String name, AbstractCacheFactory cacheFactory) {
        super(name, cacheFactory);
    }

    /**
     * Redis' host and port scheme (format {@code host:port}).
     * 
     * @return
     * @since 0.4.1
     */
    public String getRedisHostAndPort() {
        return redisHostAndPort;
    }

    /**
     * Sets Redis' host and port scheme (format {@code host:port}).
     * 
     * @param redisHostAndPort
     * @return
     * @since 0.4.1
     */
    public RedisCache setRedisHostsAndPorts(String redisHostAndPort) {
        this.redisHostAndPort = redisHostAndPort;
        return this;
    }

    /**
     * @return
     * @since 0.4.1
     */
    protected JedisPool getJedisPool() {
        return jedisPool;
    }

    /**
     * @param jedisPool
     * @return
     * @since 0.4.1
     */
    public RedisCache setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        myOwnJedisPool = false;
        return this;
    }

    public String getRedisPassword() {
        return redisPassword;
    }

    public RedisCache setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
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

        if (jedisPool == null) {
            jedisPool = RedisCacheFactory.newJedisPool(redisHostAndPort, redisPassword);
            myOwnJedisPool = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        if (jedisPool != null && myOwnJedisPool) {
            try {
                jedisPool.destroy();
            } catch (Exception e) {
            } finally {
                jedisPool = null;
            }
        }
    }

    /**
     * @return
     * @since 0.4.1
     */
    protected Jedis getJedis() {
        return jedisPool.getResource();
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
        try (Jedis jedis = getJedis()) {
            return compactMode ? jedis.hlen(getName()) : -1;
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

    public final static long TTL_NO_CHANGE = 0;
    public final static long TTL_FOREVER = -1;

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String key, Object entry, long expireAfterWrite, long expireAfterAccess) {
        try (Jedis jedis = getJedis()) {
            final String KEY = compactMode ? getName() : genCacheKeyNonCompact(key);
            final long currentTTL = jedis.ttl(KEY);
            long ttl = TTL_NO_CHANGE;
            if (!(entry instanceof CacheEntry)) {
                CacheEntry ce = new CacheEntry(key, entry, expireAfterWrite, expireAfterAccess);
                entry = ce;
                ttl = expireAfterAccess > 0 ? expireAfterAccess
                        : (expireAfterWrite > 0 ? expireAfterWrite
                                : (timeToLiveSeconds > 0 ? timeToLiveSeconds : 0));
            } else {
                CacheEntry ce = (CacheEntry) entry;
                ttl = ce.getExpireAfterAccess();
            }
            byte[] data = serializeCacheEntry((CacheEntry) entry);

            // TTL Rules:
            // 1. New item: TTL is calculated as formula(s) above.
            // 2. Old item:
            // 2.1. If [compactMode=true]: extends the current TTL
            // 2.2. If [compactMode=false]: extends the current TTL only
            // when expireAfterAccess > 0
            if (compactMode && currentTTL >= -1) {
                // old item, 2.1
                ttl = currentTTL > 0 ? currentTTL : TTL_NO_CHANGE;
            }
            if (!compactMode && currentTTL >= -1) {
                // old item, 2.2
                ttl = expireAfterAccess > 0 ? expireAfterAccess : TTL_NO_CHANGE;
            }

            if (compactMode) {
                jedis.hset(SafeEncoder.encode(KEY), SafeEncoder.encode(key), data);
                if (ttl > 0) {
                    jedis.expire(KEY, (int) ttl);
                } else if (ttl == TTL_FOREVER && currentTTL >= -1) {
                    jedis.persist(KEY);
                }
            } else {
                if (ttl > 0) {
                    jedis.setex(SafeEncoder.encode(KEY), (int) ttl, data);
                } else {
                    jedis.set(SafeEncoder.encode(KEY), data);
                    if (ttl == TTL_FOREVER) {
                        jedis.persist(KEY);
                    } else if (currentTTL > 0) {
                        jedis.expire(KEY, (int) currentTTL);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String key) {
        try (Jedis jedis = getJedis()) {
            if (compactMode) {
                jedis.hdel(getName(), key);
            } else {
                jedis.del(genCacheKeyNonCompact(key));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        try (Jedis jedis = getJedis()) {
            if (compactMode) {
                jedis.del(getName());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String key) {
        try (Jedis jedis = getJedis()) {
            if (compactMode) {
                return jedis.hget(SafeEncoder.encode(getName()), SafeEncoder.encode(key)) != null;
            } else {
                return jedis.get(SafeEncoder.encode(genCacheKeyNonCompact(key))) != null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object internalGet(String key) {
        try (Jedis jedis = getJedis()) {
            final String KEY = compactMode ? getName() : genCacheKeyNonCompact(key);
            byte[] obj = compactMode ? jedis.hget(SafeEncoder.encode(KEY), SafeEncoder.encode(key))
                    : jedis.get(SafeEncoder.encode(KEY));
            if (obj != null) {
                CacheEntry ce = deserializeCacheEntry(obj);
                if (ce != null && ce.touch()) {
                    set(key, ce, ce.getExpireAfterWrite(), ce.getExpireAfterAccess());
                }
                return ce;
            }
            return null;
        }
    }
}
