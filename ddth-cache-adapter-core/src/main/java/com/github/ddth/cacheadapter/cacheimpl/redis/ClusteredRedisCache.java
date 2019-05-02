package com.github.ddth.cacheadapter.cacheimpl.redis;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ddth.cacheadapter.AbstractCacheFactory;
import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.CacheException;
import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.ICacheEntrySerializer;
import com.github.ddth.cacheadapter.ICacheLoader;
import com.github.ddth.commons.redis.JedisConnector;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.util.SafeEncoder;

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

    private final Logger LOGGER = LoggerFactory.getLogger(ClusteredRedisCache.class);

    /**
     * To override the {@link #setRedisHostsAndPorts(String)} setting.
     * 
     * @since 0.6.1
     */
    public final static String CACHE_PROP_REDIS_HOSTS_AND_PORTS = "cache.hosts_and_ports";

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
     * Set Redis' hosts and ports scheme (format
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
     * {@inheritDoc}
     */
    @Override
    public ClusteredRedisCache init() {
        super.init();

        /*
         * Parse custom property: redis-hosts-and-ports
         */
        String hostsAndPorts = getCacheProperty(CACHE_PROP_REDIS_HOSTS_AND_PORTS);
        if (!StringUtils.isBlank(hostsAndPorts)) {
            this.redisHostsAndPorts = hostsAndPorts;
        }

        if (getJedisConnector() == null) {
            try {
                JedisConnector jedisConnector = new JedisConnector();
                jedisConnector.setRedisHostsAndPorts(redisHostsAndPorts).init();
                setJedisConnector(jedisConnector, true);
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        return this;
    }

    /**
     * @return
     * @since 0.6.3
     */
    protected JedisCluster getJedis() {
        return getJedisConnector().getJedisCluster();
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Only {@link KeyMode#HASH} is supported. This method returns {@code -1}
     * for other key modes.
     * </p>
     */
    @Override
    public long getSize() {
        switch (keyMode) {
        case HASH:
            try {
                return getJedis().hlen(getName());
            } catch (Exception e) {
                throw e instanceof CacheException ? (CacheException) e : new CacheException(e);
            }
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
        try {
            JedisCluster jedisCluster = getJedis();
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
            byte[] data = serialize((CacheEntry) entry);

            // TTL Rules:
            // 1. New item: TTL is calculated as formula(s) above.
            // 2. Existing item:
            // 2.1. If [keyMode=HASH]: extends the current TTL,
            // 2.2. Otherwise, extends the current TTL only when
            // expireAfterAccess >
            // 0
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
        } catch (Exception e) {
            throw e instanceof CacheException ? (CacheException) e : new CacheException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String key) {
        final String KEY = calcCacheKey(key);
        try {
            if (keyMode == KeyMode.HASH) {
                getJedis().hdel(getName(), KEY);
            } else {
                getJedis().del(KEY);
            }
        } catch (Exception e) {
            throw e instanceof CacheException ? (CacheException) e : new CacheException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        switch (keyMode) {
        case NAMESPACE:
            throw new CacheException.OperationNotSupportedException(
                    "Key mode[" + keyMode + "] does not support 'deleteAll' operation.");
        case MONOPOLISTIC:
            try {
                getJedis().getClusterNodes().forEach((name, pool) -> {
                    try (Jedis jedis = pool.getResource()) {
                        jedis.flushAll();
                    }
                });
            } catch (Exception e) {
                throw e instanceof CacheException ? (CacheException) e : new CacheException(e);
            }
            break;
        case HASH:
            try {
                getJedis().del(getName());
            } catch (Exception e) {
                throw e instanceof CacheException ? (CacheException) e : new CacheException(e);
            }
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
        try {
            if (keyMode == KeyMode.HASH) {
                return getJedis().hget(SafeEncoder.encode(getName()),
                        SafeEncoder.encode(KEY)) != null;
            } else {
                return getJedis().get(SafeEncoder.encode(KEY)) != null;
            }
        } catch (Exception e) {
            throw e instanceof CacheException ? (CacheException) e : new CacheException(e);
        }
    }

    /**
     * Refresh TTL of a cache entry.
     * 
     * @param key
     * @param ce
     * @since 0.6.2
     */
    protected void refreshTTL(String key, CacheEntry ce) {
        final String KEY = calcCacheKey(key);
        final long TTL = ce.getExpireAfterAccess();
        if (TTL > 0) {
            try {
                if (keyMode == KeyMode.HASH) {
                    getJedis().expire(getName(), (int) TTL);
                } else {
                    getJedis().expire(SafeEncoder.encode(KEY), (int) TTL);
                }
            } catch (Exception e) {
                throw e instanceof CacheException ? (CacheException) e : new CacheException(e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object internalGet(String key) {
        final String KEY = calcCacheKey(key);
        try {
            byte[] data = keyMode == KeyMode.HASH
                    ? getJedis().hget(SafeEncoder.encode(getName()), SafeEncoder.encode(KEY))
                    : getJedis().get(SafeEncoder.encode(KEY));
            if (data != null) {
                CacheEntry ce = deserialize(data);
                if (ce != null && ce.touch()) {
                    /*
                     * Since v0.6.2: use refreshTTL() instead of set() to save
                     * traffic between client & Redis server
                     */
                    // set(key, ce, ce.getExpireAfterWrite(),
                    // ce.getExpireAfterAccess());
                    refreshTTL(key, ce);
                }
                return ce;
            }
            return null;
        } catch (Exception e) {
            throw e instanceof CacheException ? (CacheException) e : new CacheException(e);
        }
    }
}
