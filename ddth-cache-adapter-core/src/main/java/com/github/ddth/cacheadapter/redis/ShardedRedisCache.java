package com.github.ddth.cacheadapter.redis;

import com.github.ddth.cacheadapter.AbstractCacheFactory;
import com.github.ddth.cacheadapter.AbstractSerializingCache;
import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.ICacheEntrySerializer;
import com.github.ddth.cacheadapter.ICacheLoader;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.SafeEncoder;

/**
 * Sharded <a href="http://redis.io">Redis</a> implementation of {@link ICache}.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.4.1
 */
public class ShardedRedisCache extends AbstractSerializingCache {

    private ShardedJedisPool jedisPool;
    private boolean myOwnJedisPool = true;
    private String redisHostsAndPorts = "localhost:6379";
    private String redisPassword;

    private long timeToLiveSeconds = -1;

    public ShardedRedisCache() {
        super();
    }

    public ShardedRedisCache(String name, AbstractCacheFactory cacheFactory,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory, cacheEntrySerializer);
    }

    public ShardedRedisCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory, capacity, cacheEntrySerializer);
    }

    public ShardedRedisCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess,
                cacheEntrySerializer);
    }

    public ShardedRedisCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess, ICacheLoader cacheLoader,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess, cacheLoader,
                cacheEntrySerializer);
    }

    public ShardedRedisCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess, ICacheLoader cacheLoader) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess, cacheLoader);
    }

    public ShardedRedisCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess);
    }

    public ShardedRedisCache(String name, AbstractCacheFactory cacheFactory, long capacity) {
        super(name, cacheFactory, capacity);
    }

    public ShardedRedisCache(String name, AbstractCacheFactory cacheFactory) {
        super(name, cacheFactory);
    }

    /**
     * Redis' hosts and ports scheme (format
     * {@code host1:port1,host2:port2,host3:port3}).
     * 
     * @return
     */
    public String getRedisHostsAndPorts() {
        return redisHostsAndPorts;
    }

    /**
     * Sets Redis' hosts and ports scheme (format
     * {@code host1:port1,host2:port2,host3:port3}).
     * 
     * @param redisHostAndPort
     * @return
     */
    public ShardedRedisCache setRedisHostsAndPorts(String redisHostAndPort) {
        this.redisHostsAndPorts = redisHostAndPort;
        return this;
    }

    /**
     * @return
     */
    protected ShardedJedisPool getJedisPool() {
        return jedisPool;
    }

    /**
     * @param jedisPool
     * @return
     */
    public ShardedRedisCache setJedisPool(ShardedJedisPool jedisPool) {
        this.jedisPool = jedisPool;
        myOwnJedisPool = false;
        return this;
    }

    public String getRedisPassword() {
        return redisPassword;
    }

    public ShardedRedisCache setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
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
            jedisPool = ShardedRedisCacheFactory.newJedisPool(redisHostsAndPorts, redisPassword);
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
     */
    protected ShardedJedis getJedis() {
        return jedisPool.getResource();
    }

    /**
     * Generates "flat" cachekey for non-compact mode.
     * 
     * @param key
     * @return
     */
    protected String genCacheKeyNonCompact(String key) {
        return getName() + "-" + key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize() {
        return -1;
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
        try (ShardedJedis jedis = getJedis()) {
            final String KEY = genCacheKeyNonCompact(key);
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
            // 2. Old item: extends the current TTL only
            // when expireAfterAccess > 0
            if (currentTTL >= -1) {
                // old item
                ttl = expireAfterAccess > 0 ? expireAfterAccess : TTL_NO_CHANGE;
            }
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String key) {
        try (ShardedJedis jedis = getJedis()) {
            jedis.del(genCacheKeyNonCompact(key));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String key) {
        try (ShardedJedis jedis = getJedis()) {
            return jedis.get(SafeEncoder.encode(genCacheKeyNonCompact(key))) != null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object internalGet(String key) {
        try (ShardedJedis jedis = getJedis()) {
            final String KEY = genCacheKeyNonCompact(key);
            byte[] obj = jedis.get(SafeEncoder.encode(KEY));
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
