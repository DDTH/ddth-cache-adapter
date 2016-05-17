package com.github.ddth.cacheadapter.redis;

import com.github.ddth.cacheadapter.AbstractSerializingCacheFactory;
import com.github.ddth.cacheadapter.ICacheFactory;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * <a href="http://redis.io">Redis</a> implementation of {@link ICacheFactory}
 * that creates {@link RedisCache} objects.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class RedisCacheFactory extends AbstractSerializingCacheFactory {

    public final static long DEFAULT_TIMEOUT_MS = 10000;

    /**
     * Creates a new {@link JedisPool}, with default database and timeout.
     * 
     * @param hostAndPort
     * @param password
     * @return
     */
    public static JedisPool newJedisPool(String hostAndPort, String password) {
        return newJedisPool(hostAndPort, password, Protocol.DEFAULT_DATABASE, DEFAULT_TIMEOUT_MS);
    }

    /**
     * Creates a new {@link JedisPool}, with specified database and default
     * timeout.
     * 
     * @param hostAndPort
     * @param password
     * @param db
     * @return
     */
    public static JedisPool newJedisPool(String hostAndPort, String password, int db) {
        return newJedisPool(hostAndPort, password, db, DEFAULT_TIMEOUT_MS);
    }

    /**
     * Creates a new {@link JedisPool} with default database and specified
     * timeout.
     * 
     * @param hostAndPort
     * @param password
     * @param timeoutMs
     * @return
     */
    public static JedisPool newJedisPool(String hostAndPort, String password, long timeoutMs) {
        return newJedisPool(hostAndPort, password, Protocol.DEFAULT_DATABASE, timeoutMs);
    }

    /**
     * Creates a new {@link JedisPool}.
     * 
     * @param hostAndPort
     * @param password
     * @param db
     * @param timeoutMs
     * @return
     */
    public static JedisPool newJedisPool(String hostAndPort, String password, int db,
            long timeoutMs) {
        final int maxTotal = Runtime.getRuntime().availableProcessors();
        final int maxIdle = maxTotal / 2;

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMinIdle(1);
        poolConfig.setMaxIdle(maxIdle > 0 ? maxIdle : 1);
        poolConfig.setMaxWaitMillis(timeoutMs);
        // poolConfig.setTestOnBorrow(true);
        poolConfig.setTestWhileIdle(true);

        String[] tokens = hostAndPort.split(":");
        String host = tokens.length > 0 ? tokens[0] : Protocol.DEFAULT_HOST;
        int port = tokens.length > 1 ? Integer.parseInt(tokens[1]) : Protocol.DEFAULT_PORT;
        JedisPool jedisPool = new JedisPool(poolConfig, host, port, Protocol.DEFAULT_TIMEOUT,
                password, db);
        return jedisPool;
    }

    private JedisPool jedisPool;
    private boolean myOwnJedisPool = true;
    private String redisHostAndPort = "localhost:6379";
    private String redisPassword;

    private boolean compactMode = false;

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
    public RedisCacheFactory setRedisHostAndPort(String redisHostAndPort) {
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
    public RedisCacheFactory setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        myOwnJedisPool = false;
        return this;
    }

    protected String getRedisPassword() {
        return redisPassword;
    }

    public RedisCacheFactory setRedisPassword(String redisPassword) {
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
        if (jedisPool == null) {
            jedisPool = RedisCacheFactory.newJedisPool(redisHostAndPort, redisPassword);
            myOwnJedisPool = true;
        }
        return this;
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
        super.destroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RedisCache createCacheInternal(String name, long capacity, long expireAfterWrite,
            long expireAfterAccess) {
        RedisCache cache = new RedisCache();
        cache.setName(name).setCapacity(capacity).setExpireAfterAccess(expireAfterAccess)
                .setExpireAfterWrite(expireAfterWrite);
        cache.setRedisHostsAndPorts(redisHostAndPort).setRedisPassword(redisPassword);
        cache.setCompactMode(getCompactMode());
        return cache;
    }

}
