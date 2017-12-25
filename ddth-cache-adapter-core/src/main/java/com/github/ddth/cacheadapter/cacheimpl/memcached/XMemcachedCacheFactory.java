package com.github.ddth.cacheadapter.cacheimpl.memcached;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ddth.cacheadapter.AbstractSerializingCacheFactory;
import com.github.ddth.cacheadapter.ICacheFactory;
import com.github.ddth.cacheadapter.cacheimpl.memcached.XMemcachedCache.KeyMode;

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

    private final Logger LOGGER = LoggerFactory.getLogger(XMemcachedCacheFactory.class);

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
    private XMemcachedConnector memcachedConnector;
    private boolean myOwnMemcachedConnector = true;
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
     * @since 0.6.3
     */
    public XMemcachedConnector getMemcachedConnector() {
        return memcachedConnector;
    }

    /**
     * @param memcachedConnector
     * @return
     * @since 0.6.3
     */
    public XMemcachedCacheFactory setMemcachedConnector(XMemcachedConnector memcachedConnector) {
        if (myOwnMemcachedConnector && this.memcachedConnector != null) {
            this.memcachedConnector.close();
        }
        this.memcachedConnector = memcachedConnector;
        myOwnMemcachedConnector = false;
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

        if (memcachedConnector == null) {
            try {
                memcachedConnector = new XMemcachedConnector();
                memcachedConnector.setMemcachedHostsAndPorts(memcachedHostsAndPorts).init();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
            myOwnMemcachedConnector = true;
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        if (memcachedConnector != null && myOwnMemcachedConnector) {
            try {
                memcachedConnector.destroy();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            } finally {
                memcachedConnector = null;
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
        cache.setMemcachedHostsAndPorts(memcachedHostsAndPorts)
                .setMemcachedConnector(memcachedConnector);
        return cache;
    }

}
