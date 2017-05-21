package com.github.ddth.cacheadapter.redis;

import java.util.Properties;

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
public class RedisCacheFactory extends BaseRedisCacheFactory {

    /**
     * Creates a new {@link JedisPool}, with default database and timeout.
     * 
     * @param hostAndPort
     * @param password
     * @return
     */
    public static JedisPool newJedisPool(String hostAndPort, String password) {
        return newJedisPool(hostAndPort, password, Protocol.DEFAULT_DATABASE,
                Protocol.DEFAULT_TIMEOUT);
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
        return newJedisPool(hostAndPort, password, db, Protocol.DEFAULT_TIMEOUT);
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
        poolConfig.setMaxWaitMillis(timeoutMs + 1000);
        // poolConfig.setTestOnBorrow(true);
        poolConfig.setTestWhileIdle(true);

        String[] tokens = hostAndPort.split(":");
        String host = tokens.length > 0 ? tokens[0] : Protocol.DEFAULT_HOST;
        int port = tokens.length > 1 ? Integer.parseInt(tokens[1]) : Protocol.DEFAULT_PORT;
        JedisPool jedisPool = new JedisPool(poolConfig, host, port, (int) timeoutMs, password, db);
        return jedisPool;
    }

    private JedisPool jedisPool;
    private String redisHostAndPort = Protocol.DEFAULT_HOST + ":" + Protocol.DEFAULT_PORT;

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
        if (myOwnRedis && this.jedisPool != null) {
            this.jedisPool.close();
        }
        this.jedisPool = jedisPool;
        myOwnRedis = false;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RedisCacheFactory init() {
        super.init();
        if (jedisPool == null && isBuildGlobalRedis()) {
            jedisPool = RedisCacheFactory.newJedisPool(redisHostAndPort, getRedisPassword());
            myOwnRedis = true;
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        if (jedisPool != null && myOwnRedis) {
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
            long expireAfterAccess, Properties cacheProps) {
        RedisCache cache = new RedisCache(keyMode);
        cache.setName(name).setCapacity(capacity).setExpireAfterAccess(expireAfterAccess)
                .setExpireAfterWrite(expireAfterWrite).setCacheProperties(cacheProps);
        cache.setRedisHostAndPort(redisHostAndPort).setRedisPassword(getRedisPassword());
        if (jedisPool == null) {
            cache.setJedisPool(jedisPool);
        }
        return cache;
    }

}
