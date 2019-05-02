package com.github.ddth.cacheadapter.cacheimpl.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ddth.cacheadapter.ICacheFactory;
import com.github.ddth.commons.redis.JedisConnector;

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
public class ShardedRedisCacheFactory extends BaseRedisCacheFactory {

    private final Logger LOGGER = LoggerFactory.getLogger(ShardedRedisCacheFactory.class);

    /**
     * Create a new {@link ShardedJedisPool}, with default timeout.
     * 
     * @param hostsAndPorts
     *            format {@code host1:port1,host2:port2...}
     * @param password
     * @return
     */
    public static ShardedJedisPool newJedisPool(String hostsAndPorts, String password) {
        return newJedisPool(hostsAndPorts, password, Protocol.DEFAULT_TIMEOUT);
    }

    /**
     * Create a new {@link ShardedJedisPool}.
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
        poolConfig.setMaxWaitMillis(timeoutMs + 1000);
        // poolConfig.setTestOnBorrow(true);
        poolConfig.setTestWhileIdle(true);

        List<JedisShardInfo> shards = new ArrayList<>();
        String[] hapList = hostsAndPorts.split("[,;\\s]+");
        for (String hostAndPort : hapList) {
            String[] tokens = hostAndPort.split(":");
            String host = tokens.length > 0 ? tokens[0] : Protocol.DEFAULT_HOST;
            int port = tokens.length > 1 ? Integer.parseInt(tokens[1]) : Protocol.DEFAULT_PORT;
            JedisShardInfo shardInfo = new JedisShardInfo(host, port, (int) timeoutMs);
            shardInfo.setPassword(password);
            shards.add(shardInfo);
        }
        ShardedJedisPool jedisPool = new ShardedJedisPool(poolConfig, shards);
        return jedisPool;
    }

    private String redisHostsAndPorts = Protocol.DEFAULT_HOST + ":" + Protocol.DEFAULT_PORT;;

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
     * {@inheritDoc}
     */
    @Override
    public ShardedRedisCacheFactory init() {
        super.init();

        if (getJedisConnector() == null) {
            try {
                JedisConnector jedisConnector = new JedisConnector();
                jedisConnector.setRedisHostsAndPorts(redisHostsAndPorts)
                        .setRedisPassword(getRedisPassword()).init();
                setJedisConnector(jedisConnector, true);
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ShardedRedisCache createCacheInternal(String name, long capacity,
            long expireAfterWrite, long expireAfterAccess, Properties cacheProps) {
        ShardedRedisCache cache = new ShardedRedisCache(keyMode);
        cache.setName(name).setCapacity(capacity).setExpireAfterAccess(expireAfterAccess)
                .setExpireAfterWrite(expireAfterWrite).setCacheProperties(cacheProps);
        cache.setRedisHostsAndPorts(redisHostsAndPorts).setRedisPassword(getRedisPassword());
        cache.setJedisConnector(getJedisConnector());
        return cache;
    }
}
