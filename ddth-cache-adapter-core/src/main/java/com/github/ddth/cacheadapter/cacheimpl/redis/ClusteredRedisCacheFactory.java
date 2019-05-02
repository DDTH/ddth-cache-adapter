package com.github.ddth.cacheadapter.cacheimpl.redis;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ddth.cacheadapter.ICacheFactory;
import com.github.ddth.commons.redis.JedisConnector;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * Clustered <a href="http://redis.io">Redis</a> implementation of
 * {@link ICacheFactory} that creates {@link ClusteredRedisCache} objects.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.4.1
 */
public class ClusteredRedisCacheFactory extends BaseRedisCacheFactory {

    private final Logger LOGGER = LoggerFactory.getLogger(ClusteredRedisCacheFactory.class);

    public final static int DEFAULT_MAX_ATTEMPTS = 3;

    /**
     * Create a new {@link JedisCluster}, with default timeout.
     * 
     * @param hostsAndPorts
     *            format {@code host1:port1,host2:port2...}
     * @param password
     * @return
     */
    public static JedisCluster newJedisCluster(String hostsAndPorts, String password) {
        return newJedisCluster(hostsAndPorts, password, Protocol.DEFAULT_TIMEOUT);
    }

    /**
     * Create a new {@link JedisCluster}.
     * 
     * @param hostsAndPorts
     *            format {@code host1:port1,host2:port2...}
     * @param password
     * @param timeoutMs
     * @return
     */
    public static JedisCluster newJedisCluster(String hostsAndPorts, String password,
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

        Set<HostAndPort> clusterNodes = new HashSet<>();
        String[] hapList = hostsAndPorts.split("[,;\\s]+");
        for (String hostAndPort : hapList) {
            String[] tokens = hostAndPort.split(":");
            String host = tokens.length > 0 ? tokens[0] : Protocol.DEFAULT_HOST;
            int port = tokens.length > 1 ? Integer.parseInt(tokens[1]) : Protocol.DEFAULT_PORT;
            clusterNodes.add(new HostAndPort(host, port));
        }

        JedisCluster jedisCluster = new JedisCluster(clusterNodes, (int) timeoutMs, (int) timeoutMs,
                DEFAULT_MAX_ATTEMPTS, password, poolConfig);
        return jedisCluster;
    }

    private String redisHostsAndPorts = Protocol.DEFAULT_HOST + ":" + Protocol.DEFAULT_PORT;

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
    public ClusteredRedisCacheFactory setRedisHostsAndPorts(String redisHostsAndPorts) {
        this.redisHostsAndPorts = redisHostsAndPorts;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClusteredRedisCacheFactory init() {
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
    protected ClusteredRedisCache createCacheInternal(String name, long capacity,
            long expireAfterWrite, long expireAfterAccess, Properties cacheProps) {
        ClusteredRedisCache cache = new ClusteredRedisCache(keyMode);
        cache.setName(name).setCapacity(capacity).setExpireAfterAccess(expireAfterAccess)
                .setExpireAfterWrite(expireAfterWrite).setCacheProperties(cacheProps);
        cache.setRedisHostsAndPorts(redisHostsAndPorts).setRedisPassword(getRedisPassword());
        cache.setJedisConnector(getJedisConnector());
        return cache;
    }
}
