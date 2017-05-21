package com.github.ddth.cacheadapter.memcached;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.github.ddth.cacheadapter.AbstractSerializingCacheFactory;
import com.github.ddth.cacheadapter.CacheException;
import com.github.ddth.cacheadapter.ICacheFactory;
import com.github.ddth.cacheadapter.memcached.XMemcachedCache.KeyMode;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;

/**
 * <a href="http://memcached.org">Memcached</a> implementation of
 * {@link ICacheFactory} that creates {@link XMemcachedCache} objects.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.0
 */
public class XMemcachedCacheFactory extends AbstractSerializingCacheFactory {

    public final static long DEFAULT_TIMEOUT_MS = 10000;

    /**
     * Creates a new {@link MemcachedClient}, with default connection timeout
     * and connection pool size of 1.
     * 
     * @param hostsAndPorts
     *            Memcached' hosts and ports scheme (format
     *            {@code host1:port1,host2:port2}).
     * @return
     * @throws IOException
     */
    public static MemcachedClient newMemcachedClient(String hostsAndPorts) throws IOException {
        return newMemcachedClient(hostsAndPorts, DEFAULT_TIMEOUT_MS, 1);
    }

    /**
     * Creates a new {@link MemcachedClient}, with specified connection timeout
     * and connection pool size.
     * 
     * @param hostsAndPorts
     *            Memcached' hosts and ports scheme (format
     *            {@code host1:port1,host2:port2}).
     * @param connTimeoutMs
     * @param connPoolSize
     * @return
     * @throws IOException
     */
    public static MemcachedClient newMemcachedClient(String hostsAndPorts, long connTimeoutMs,
            int connPoolSize) throws IOException {
        hostsAndPorts = StringUtils.join(hostsAndPorts.split("[\\s,;]+"), ' ');
        MemcachedClientBuilder builder = new XMemcachedClientBuilder(
                AddrUtil.getAddresses(hostsAndPorts));
        builder.setConnectTimeout(connTimeoutMs);
        builder.setConnectionPoolSize(connPoolSize);
        return builder.build();
    }

    private KeyMode keyMode = KeyMode.NAMESPACE;
    private MemcachedClient memcachedClient;
    private boolean myOwnMemcachedClient = true;
    private String memcachedHostsAndPorts = "localhost:11211";

    /**
     * Get Memcached' hosts and ports scheme (format
     * {@code host1:port1,host2:port2}).
     * 
     * @return
     */
    public String getMemcachedHostsAndPorts() {
        return memcachedHostsAndPorts;
    }

    /**
     * Set Memcached' hosts and ports scheme (format
     * {@code host1:port1,host2:port2}).
     * 
     * @param memcachedHostsAndPorts
     * @return
     */
    public XMemcachedCacheFactory setMemcachedHostsAndPorts(String memcachedHostsAndPorts) {
        this.memcachedHostsAndPorts = memcachedHostsAndPorts;
        return this;
    }

    /**
     * @return
     */
    protected MemcachedClient getMemcachedClient() {
        return memcachedClient;
    }

    /**
     * @param memcachedClient
     * @return
     */
    public XMemcachedCacheFactory setMemcachedClient(MemcachedClient memcachedClient) {
        if (myOwnMemcachedClient && this.memcachedClient != null) {
            try {
                this.memcachedClient.shutdown();
            } catch (IOException e) {
                throw new CacheException(e);
            }
        }
        this.memcachedClient = memcachedClient;
        myOwnMemcachedClient = false;
        return this;
    }

    public KeyMode getKeyMode() {
        return keyMode;
    }

    public XMemcachedCacheFactory setKeyMode(KeyMode keyMode) {
        this.keyMode = keyMode;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XMemcachedCacheFactory init() {
        super.init();
        if (memcachedClient == null) {
            try {
                memcachedClient = newMemcachedClient(memcachedHostsAndPorts);
            } catch (IOException e) {
                throw new CacheException(e);
            }
            myOwnMemcachedClient = true;
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        if (memcachedClient != null && myOwnMemcachedClient) {
            try {
                memcachedClient.shutdown();
            } catch (Exception e) {
            } finally {
                memcachedClient = null;
            }
        }
        super.destroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected XMemcachedCache createCacheInternal(String name, long capacity, long expireAfterWrite,
            long expireAfterAccess, Properties cacheProps) {
        XMemcachedCache cache = new XMemcachedCache(keyMode);
        cache.setName(name).setCapacity(capacity).setExpireAfterAccess(expireAfterAccess)
                .setExpireAfterWrite(expireAfterWrite).setCacheProperties(cacheProps);
        cache.setMemcachedHostsAndPorts(memcachedHostsAndPorts);
        return cache;
    }

}
