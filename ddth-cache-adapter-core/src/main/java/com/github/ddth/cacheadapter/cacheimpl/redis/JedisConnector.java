package com.github.ddth.cacheadapter.cacheimpl.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ddth.cacheadapter.CacheException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * Manage connection to memcached server.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.3
 */
public class JedisConnector implements AutoCloseable {

    private final Logger LOGGER = LoggerFactory.getLogger(JedisConnector.class);

    private String redisHostsAndPorts = Protocol.DEFAULT_HOST + ":" + Protocol.DEFAULT_PORT;
    private String redisPassword;

    private JedisPool jedisPool;
    private JedisCluster jedisCluster;
    private ShardedJedisPool shardedJedisPool;

    /**
     * Get Redis' hosts and ports scheme (format
     * {@code host1:port1,host2:port2}).
     * 
     * @return
     */
    public String getRedisHostsAndPorts() {
        return redisHostsAndPorts;
    }

    /**
     * Set Redis' hosts and ports scheme (format
     * {@code host1:port1,host2:port2}).
     * 
     * @param redisHostsAndPorts
     * @return
     */
    public JedisConnector setRedisHostsAndPorts(String redisHostsAndPorts) {
        this.redisHostsAndPorts = redisHostsAndPorts;
        return this;
    }

    public String getRedisPassword() {
        return redisPassword;
    }

    public JedisConnector setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
        return this;
    }

    synchronized private void connectJedisPool() throws CacheException {
        if (jedisPool == null) {
            String[] tokens = redisHostsAndPorts.split("[,;\\s]+");
            jedisPool = RedisCacheFactory.newJedisPool(tokens[0], getRedisPassword());
        }
    }

    public Jedis getJedis() {
        if (jedisPool == null) {
            connectJedisPool();
        }
        return jedisPool.getResource();
    }

    synchronized private void connectJedisCluster() throws CacheException {
        if (jedisCluster == null) {
            jedisCluster = ClusteredRedisCacheFactory.newJedisCluster(redisHostsAndPorts,
                    getRedisPassword());
        }
    }

    public JedisCluster getJedisCluster() {
        if (jedisCluster == null) {
            connectJedisCluster();
        }
        return jedisCluster;
    }

    synchronized private void connectShardedJedisPool() throws CacheException {
        if (shardedJedisPool == null) {
            shardedJedisPool = ShardedRedisCacheFactory.newJedisPool(redisHostsAndPorts,
                    getRedisPassword());
        }
    }

    public ShardedJedis getShardedJedis() {
        if (shardedJedisPool == null) {
            connectShardedJedisPool();
        }
        return shardedJedisPool.getResource();
    }

    public JedisConnector init() {
        return this;
    }

    public void destroy() {
        if (jedisPool != null) {
            try {
                jedisPool.close();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            } finally {
                jedisPool = null;
            }
        }

        if (jedisCluster != null) {
            try {
                jedisCluster.close();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            } finally {
                jedisCluster = null;
            }
        }

        if (shardedJedisPool != null) {
            try {
                shardedJedisPool.close();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            } finally {
                shardedJedisPool = null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        destroy();
    }

}
