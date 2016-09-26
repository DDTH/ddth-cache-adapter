package com.github.ddth.cacheadapter.redis;

import com.github.ddth.cacheadapter.AbstractCacheFactory;
import com.github.ddth.cacheadapter.AbstractSerializingCache;
import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.ICacheEntrySerializer;
import com.github.ddth.cacheadapter.ICacheLoader;

import redis.clients.jedis.JedisCluster;
import redis.clients.util.SafeEncoder;

/**
 * Clustered <a href="http://redis.io">Redis</a> implementation of
 * {@link ICache}.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.4.1
 */
public class ClusteredRedisCache extends AbstractSerializingCache {

    private JedisCluster jedisCluster;
    private boolean myOwnJedisCluster = true;
    private String redisHostsAndPorts = "localhost:6379";

    private long timeToLiveSeconds = -1;

    public ClusteredRedisCache() {
        super();
    }

    public ClusteredRedisCache(String name, AbstractCacheFactory cacheFactory,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory, cacheEntrySerializer);
    }

    public ClusteredRedisCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory, capacity, cacheEntrySerializer);
    }

    public ClusteredRedisCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess,
                cacheEntrySerializer);
    }

    public ClusteredRedisCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess, ICacheLoader cacheLoader,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess, cacheLoader,
                cacheEntrySerializer);
    }

    public ClusteredRedisCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess, ICacheLoader cacheLoader) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess, cacheLoader);
    }

    public ClusteredRedisCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess);
    }

    public ClusteredRedisCache(String name, AbstractCacheFactory cacheFactory, long capacity) {
        super(name, cacheFactory, capacity);
    }

    public ClusteredRedisCache(String name, AbstractCacheFactory cacheFactory) {
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
    public ClusteredRedisCache setRedisHostsAndPorts(String redisHostAndPort) {
        this.redisHostsAndPorts = redisHostAndPort;
        return this;
    }

    /**
     * @return
     */
    protected JedisCluster getJedisCluster() {
        return jedisCluster;
    }

    /**
     * @param jedisPool
     * @return
     */
    public ClusteredRedisCache setJedisCluster(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
        myOwnJedisCluster = false;
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

        if (jedisCluster == null) {
            jedisCluster = ClusteredRedisCacheFactory.newJedisCluster(redisHostsAndPorts);
            myOwnJedisCluster = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        if (jedisCluster != null && myOwnJedisCluster) {
            try {
                jedisCluster.close();
            } catch (Exception e) {
            } finally {
                jedisCluster = null;
            }
        }
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
        final String KEY = genCacheKeyNonCompact(key);
        final long currentTTL = jedisCluster.ttl(KEY);
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
            jedisCluster.setex(SafeEncoder.encode(KEY), (int) ttl, data);
        } else {
            jedisCluster.set(SafeEncoder.encode(KEY), data);
            if (ttl == TTL_FOREVER) {
                jedisCluster.persist(KEY);
            } else if (currentTTL > 0) {
                jedisCluster.expire(KEY, (int) currentTTL);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String key) {
        jedisCluster.del(genCacheKeyNonCompact(key));
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
        return jedisCluster.get(SafeEncoder.encode(genCacheKeyNonCompact(key))) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object internalGet(String key) {
        final String KEY = genCacheKeyNonCompact(key);
        byte[] obj = jedisCluster.get(SafeEncoder.encode(KEY));
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
