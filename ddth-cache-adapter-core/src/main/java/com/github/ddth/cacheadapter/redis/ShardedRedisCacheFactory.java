package com.github.ddth.cacheadapter.redis;

import java.util.ArrayList;
import java.util.List;

import com.github.ddth.cacheadapter.AbstractSerializingCacheFactory;
import com.github.ddth.cacheadapter.ICacheFactory;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.ShardedJedisPool;

/**
 * Sharded <a href="http://redis.io">Redis</a> implementation of
 * {@link ICacheFactory} that creates {@link ShardedRedisCache} objects.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.4.1
 */
public class ShardedRedisCacheFactory extends AbstractSerializingCacheFactory {

    public final static long DEFAULT_TIMEOUT_MS = 10000;

    /**
     * Creates a new {@link ShardedJedisPool}, with default timeout.
     * 
     * @param hostsAndPorts
     *            format {@code host1:port1,host2:port2...}
     * @param password
     * @return
     */
    public static ShardedJedisPool newJedisPool(String hostsAndPorts, String password) {
        return newJedisPool(hostsAndPorts, password, DEFAULT_TIMEOUT_MS);
    }

    /**
     * Creates a new {@link ShardedJedisPool}.
     * 
     * @param hostsAndPorts
     *            format {@code host1:port1,host2:port2...}
     * @param password
     * @param timeoutMs
     * @return
     */
    public static ShardedJedisPool newJedisPool(String hostsAndPorts, String password,
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

        List<JedisShardInfo> shards = new ArrayList<>();
        String[] hapList = hostsAndPorts.split("[,;\\s]+");
        for (String hostAndPort : hapList) {
            String[] tokens = hostAndPort.split(":");
            String host = tokens.length > 0 ? tokens[0] : Protocol.DEFAULT_HOST;
            int port = tokens.length > 1 ? Integer.parseInt(tokens[1]) : Protocol.DEFAULT_PORT;
            JedisShardInfo shardInfo = new JedisShardInfo(host, port);
            shardInfo.setPassword(password);
            shards.add(shardInfo);
        }
        ShardedJedisPool jedisPool = new ShardedJedisPool(poolConfig, shards);

        return jedisPool;
    }

    private ShardedJedisPool jedisPool;
    private boolean myOwnJedisPool = true;
    private String redisHostsAndPorts = "localhost:6379";
    private String redisPassword;

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
     * Redis' hosts and ports scheme (format
     * {@code host1:port1,host2:port2,host3:port3}).
     * 
     * @param redisHostsAndPorts
     * @return
     */
    public ShardedRedisCacheFactory setRedisHostsAndPorts(String redisHostsAndPorts) {
        this.redisHostsAndPorts = redisHostsAndPorts;
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
    public ShardedRedisCacheFactory setJedisPool(ShardedJedisPool jedisPool) {
        this.jedisPool = jedisPool;
        myOwnJedisPool = false;
        return this;
    }

    protected String getRedisPassword() {
        return redisPassword;
    }

    public ShardedRedisCacheFactory setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShardedRedisCacheFactory init() {
        super.init();
        if (jedisPool == null) {
            jedisPool = ShardedRedisCacheFactory.newJedisPool(redisHostsAndPorts, redisPassword);
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
    protected ShardedRedisCache createCacheInternal(String name, long capacity,
            long expireAfterWrite, long expireAfterAccess) {
        ShardedRedisCache cache = new ShardedRedisCache();
        cache.setName(name).setCapacity(capacity).setExpireAfterAccess(expireAfterAccess)
                .setExpireAfterWrite(expireAfterWrite);
        cache.setRedisHostsAndPorts(redisHostsAndPorts).setRedisPassword(redisPassword);
        return cache;
    }

}
