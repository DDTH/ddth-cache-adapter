package com.github.ddth.cacheadapter.redis;

import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.github.ddth.cacheadapter.CacheException;
import com.github.ddth.cacheadapter.ICacheFactory;

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

    public final static int DEFAULT_MAX_ATTEMPTS = 3;

    /**
     * Creates a new {@link JedisCluster}, with default timeout.
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
     * Creates a new {@link JedisCluster}.
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

    private JedisCluster jedisCluster;
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
     * @return
     */
    protected JedisCluster getJedisCluster() {
        return jedisCluster;
    }

    /**
     * @param jedisPool
     * @return
     */
    public ClusteredRedisCacheFactory setJedisCluster(JedisCluster jedisCluster) {
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
    public ClusteredRedisCacheFactory init() {
        super.init();
        if (jedisCluster == null && isBuildGlobalRedis()) {
            jedisCluster = ClusteredRedisCacheFactory.newJedisCluster(redisHostsAndPorts,
                    getRedisPassword());
            myOwnRedis = true;
        }
        return this;
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
        super.destroy();
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
        if (jedisCluster == null) {
            cache.setJedisCluster(jedisCluster);
        }
        return cache;
    }

}
