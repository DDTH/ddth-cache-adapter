package com.github.ddth.cacheadapter.cacheimpl.memcached;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ddth.cacheadapter.CacheException;

import net.rubyeye.xmemcached.MemcachedClient;

/**
 * Manage connections to memcached server.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.3
 */
public class XMemcachedConnector implements AutoCloseable {

    private final Logger LOGGER = LoggerFactory.getLogger(XMemcachedConnector.class);

    private MemcachedClient memcachedClient;
    private String memcachedHostsAndPorts = "localhost:11211";

    /**
     * Get Memcached's hosts and ports scheme (format
     * {@code host1:port1,host2:port2}).
     * 
     * @return
     */
    public String getMemcachedHostsAndPorts() {
        return memcachedHostsAndPorts;
    }

    /**
     * Set Memcached's hosts and ports scheme (format
     * {@code host1:port1,host2:port2}).
     * 
     * @param memcachedHostsAndPorts
     * @return
     */
    public XMemcachedConnector setMemcachedHostsAndPorts(String memcachedHostsAndPorts) {
        this.memcachedHostsAndPorts = memcachedHostsAndPorts;
        return this;
    }

    synchronized private void connect() throws CacheException {
        if (memcachedClient == null) {
            try {
                memcachedClient = XMemcachedCacheFactory.newMemcachedClient(memcachedHostsAndPorts);
            } catch (Exception e) {
                throw e instanceof CacheException ? (CacheException) e : new CacheException(e);
            }
        }
    }

    /**
     * Obtain a {@link MemcachedClient} instance.
     * 
     * @return
     */
    public MemcachedClient getMemcachedClient() {
        if (memcachedClient == null) {
            connect();
        }
        return memcachedClient;
    }

    public XMemcachedConnector init() {
        return this;
    }

    public void destroy() {
        if (memcachedClient != null) {
            try {
                memcachedClient.shutdown();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            } finally {
                memcachedClient = null;
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
