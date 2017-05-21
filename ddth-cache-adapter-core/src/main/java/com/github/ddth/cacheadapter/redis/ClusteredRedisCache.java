package com.github.ddth.cacheadapter.redis;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.github.ddth.cacheadapter.AbstractCacheFactory;
import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.CacheException;
import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.ICacheEntrySerializer;
import com.github.ddth.cacheadapter.ICacheLoader;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Protocol;
import redis.clients.util.SafeEncoder;

/**
 * Clustered <a href="http://redis.io">Redis</a> implementation of
 * {@link ICache}.
 * 
 * <p>
 * This Redis-based cache implementation supports all types of {@link KeyMode}.
 * </p>
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.4.1
 */
public class ClusteredRedisCache extends BaseRedisCache {

    /**
     * To override the {@link #setRedisHostsAndPorts(String)} setting.
     * 
     * @since 0.6.1
     */
    public final static String CACHE_PROP_REDIS_HOSTS_AND_PORTS = "cache.hosts_and_ports";

    private JedisCluster jedisCluster;
    private String redisHostsAndPorts = Protocol.DEFAULT_HOST + ":" + Protocol.DEFAULT_PORT;

    public ClusteredRedisCache(KeyMode keyMode) {
        super(keyMode);
    }

    public ClusteredRedisCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(keyMode, name, cacheFactory, cacheEntrySerializer);
    }

    public ClusteredRedisCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory,
            long capacity, ICacheEntrySerializer cacheEntrySerializer) {
        super(keyMode, name, cacheFactory, capacity, cacheEntrySerializer);
    }

    public ClusteredRedisCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory,
            long capacity, long expireAfterWrite, long expireAfterAccess,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(keyMode, name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess,
                cacheEntrySerializer);
    }

    public ClusteredRedisCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory,
            long capacity, long expireAfterWrite, long expireAfterAccess, ICacheLoader cacheLoader,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(keyMode, name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess,
                cacheLoader, cacheEntrySerializer);
    }

    public ClusteredRedisCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory,
            long capacity, long expireAfterWrite, long expireAfterAccess,
            ICacheLoader cacheLoader) {
        super(keyMode, name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess,
                cacheLoader);
    }

    public ClusteredRedisCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory,
            long capacity, long expireAfterWrite, long expireAfterAccess) {
        super(keyMode, name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess);
    }

    public ClusteredRedisCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory,
            long capacity) {
        super(keyMode, name, cacheFactory, capacity);
    }

    public ClusteredRedisCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory) {
        super(keyMode, name, cacheFactory);
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
     * @param redisHostsAndPorts
     * @return
     */
    public ClusteredRedisCache setRedisHostsAndPorts(String redisHostsAndPorts) {
        this.redisHostsAndPorts = redisHostsAndPorts;
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
        if (myOwnRedis && this.jedisCluster != null) {
            try {
                this.jedisCluster.close();
            } catch (IOException e) {
                throw new CacheException(e);
            }
        }
        this.jedisCluster = jedisCluster;
        myOwnRedis = false;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        super.init();

        /*
         * Parse custom property: redis-hosts-and-ports
         */
        String hostsAndPorts = getCacheProperty(CACHE_PROP_REDIS_HOSTS_AND_PORTS);
        if (!StringUtils.isBlank(hostsAndPorts)) {
            this.redisHostsAndPorts = hostsAndPorts;
        }

        if (jedisCluster == null) {
            jedisCluster = ClusteredRedisCacheFactory.newJedisCluster(redisHostsAndPorts,
                    getRedisPassword());
            myOwnRedis = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        if (jedisCluster != null && myOwnRedis) {
            try {
                jedisCluster.close();
            } catch (Exception e) {
            } finally {
                jedisCluster = null;
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Only {@link KeyMode#HASH} is supported. This method returns {@code -1} for other key modes.
     * </p>
     */
    @Override
    public long getSize() {
        switch (keyMode) {
        case HASH:
            return jedisCluster.hlen(getName());
        default:
            return -1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String key, Object entry, long expireAfterWrite, long expireAfterAccess) {
        final String KEY = calcCacheKey(key);
        /*
         * Returns -1 if no expiry, -2 if key does not exist.
         */
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
        // 2. Existing item:
        // 2.1. If [keyMode=HASH]: extends the current TTL,
        // 2.2. Otherwise, extends the current TTL only when expireAfterAccess > 0
        if (currentTTL >= -1) {
            // existing item
            if (keyMode == KeyMode.HASH)
                // rule 2.1
                ttl = currentTTL > 0 ? currentTTL : TTL_NO_CHANGE;
            else
                // rule 2.2
                ttl = expireAfterAccess > 0 ? expireAfterAccess : TTL_NO_CHANGE;
        }

        if (keyMode == KeyMode.HASH) {
            jedisCluster.hset(SafeEncoder.encode(getName()), SafeEncoder.encode(KEY), data);
            if (ttl > 0) {
                jedisCluster.expire(getName(), (int) ttl);
            } else if (ttl == TTL_FOREVER && currentTTL >= -1) {
                jedisCluster.persist(getName());
            }
        } else {
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String key) {
        final String KEY = calcCacheKey(key);
        if (keyMode == KeyMode.HASH) {
            jedisCluster.hdel(getName(), KEY);
        } else {
            jedisCluster.del(KEY);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        switch (keyMode) {
        case NAMESPACE:
        case MONOPOLISTIC:
            throw new CacheException.OperationNotSupportedException(
                    "Key mode[" + keyMode + "] does not support flushall operation.");
        case HASH:
            jedisCluster.del(getName());
            break;
        default:
            throw new IllegalStateException("Invalid key mode: " + keyMode);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String key) {
        final String KEY = calcCacheKey(key);
        if (keyMode == KeyMode.HASH) {
            return jedisCluster.hget(SafeEncoder.encode(getName()),
                    SafeEncoder.encode(KEY)) != null;
        } else {
            return jedisCluster.get(SafeEncoder.encode(KEY)) != null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object internalGet(String key) {
        final String KEY = calcCacheKey(key);
        byte[] data = keyMode == KeyMode.HASH
                ? jedisCluster.hget(SafeEncoder.encode(getName()), SafeEncoder.encode(KEY))
                : jedisCluster.get(SafeEncoder.encode(KEY));
        if (data != null) {
            CacheEntry ce = deserializeCacheEntry(data);
            if (ce != null && ce.touch()) {
                set(key, ce, ce.getExpireAfterWrite(), ce.getExpireAfterAccess());
            }
            return ce;
        }
        return null;
    }
}
