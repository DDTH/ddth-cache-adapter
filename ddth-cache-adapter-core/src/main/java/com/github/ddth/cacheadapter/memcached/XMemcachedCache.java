package com.github.ddth.cacheadapter.memcached;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ddth.cacheadapter.AbstractCacheFactory;
import com.github.ddth.cacheadapter.AbstractSerializingCache;
import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.CacheException;
import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.ICacheEntrySerializer;
import com.github.ddth.cacheadapter.ICacheLoader;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientCallable;
import net.rubyeye.xmemcached.exception.MemcachedException;

/**
 * <a href="http://memcached.org">Memcached</a> implementation of {@link ICache}
 * using <a href="https://github.com/killme2008/xmemcached">XMemcached
 * library</a>.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.6.0
 */
public class XMemcachedCache extends AbstractSerializingCache {

    private final Logger LOGGER = LoggerFactory.getLogger(XMemcachedCache.class);

    /**
     * Defines how cache key/entries are grouped together.
     */
    public enum KeyMode {
        /**
         * Cache entries are grouped into namespaces: cache keys are prefixed
         * with cache's name. So more than one {@link XMemcachedCache} instances
         * can share one memcached server.
         */
        NAMESPACE(0),

        /**
         * Assuming the whole memcached server is dedicated to the
         * {@link XMemcachedCache} instance. Cache keys are kept as-is.
         */
        MONOPOLISTIC(1),

        /**
         * Cache entries are grouped into namespaces: using XMemcached's
         * namespace mechanism.
         */
        XNAMESPACE(2);

        public final int value;

        KeyMode(int value) {
            this.value = value;
        }
    }

    /**
     * To override the {@link #keyMode} setting.
     * 
     * @since 0.6.1
     */
    public final static String CACHE_PROP_KEY_MODE = "cache.key_mode";

    /**
     * To override the {@link #setMemcachedHostsAndPorts(String)} setting.
     * 
     * @since 0.6.1
     */
    public final static String CACHE_PROP_MEMCACHED_HOSTS_AND_PORTS = "cache.hosts_and_ports";

    /**
     * To override the {@link #timeToLiveSeconds} setting.
     * 
     * @since 0.6.1
     */
    public final static String CACHE_PROP_TTL_SECONDS = "cache.ttl_seconds";

    private KeyMode keyMode;

    private MemcachedClient memcachedClient;
    private boolean myOwnMemcachedClient = true;
    private String memcachedHostsAndPorts = "localhost:11211";
    private long timeToLiveSeconds = -1;

    public XMemcachedCache(KeyMode keyMode) {
        super();
        this.keyMode = keyMode;
    }

    public XMemcachedCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory, cacheEntrySerializer);
        this.keyMode = keyMode;
    }

    public XMemcachedCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory,
            long capacity, ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory, capacity, cacheEntrySerializer);
        this.keyMode = keyMode;
    }

    public XMemcachedCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory,
            long capacity, long expireAfterWrite, long expireAfterAccess,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess,
                cacheEntrySerializer);
        this.keyMode = keyMode;
    }

    public XMemcachedCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory,
            long capacity, long expireAfterWrite, long expireAfterAccess, ICacheLoader cacheLoader,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess, cacheLoader,
                cacheEntrySerializer);
        this.keyMode = keyMode;
    }

    public XMemcachedCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory,
            long capacity, long expireAfterWrite, long expireAfterAccess,
            ICacheLoader cacheLoader) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess, cacheLoader);
        this.keyMode = keyMode;
    }

    public XMemcachedCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory,
            long capacity, long expireAfterWrite, long expireAfterAccess) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess);
        this.keyMode = keyMode;
    }

    public XMemcachedCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory,
            long capacity) {
        super(name, cacheFactory, capacity);
        this.keyMode = keyMode;
    }

    public XMemcachedCache(KeyMode keyMode, String name, AbstractCacheFactory cacheFactory) {
        super(name, cacheFactory);
        this.keyMode = keyMode;
    }

    /**
     * 
     * @return
     * @see KeyMode
     */
    public KeyMode getKeyMode() {
        return keyMode;
    }

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
    public XMemcachedCache setMemcachedHostsAndPorts(String memcachedHostsAndPorts) {
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
    public XMemcachedCache setMemcachedClient(MemcachedClient memcachedClient) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        super.init();
        long expireAfterWrite = getExpireAfterWrite();
        long expireAfterAccess = getExpireAfterAccess();
        if (expireAfterAccess > 0 || expireAfterWrite > 0) {
            timeToLiveSeconds = expireAfterAccess > 0 ? expireAfterAccess : expireAfterWrite;
        } else {
            timeToLiveSeconds = -1;
        }

        /*
         * Parse custom property: key-mode
         */
        KeyMode oldKeyMode = this.keyMode;
        try {
            this.keyMode = KeyMode.valueOf(getCacheProperty(CACHE_PROP_KEY_MODE).toUpperCase());
        } catch (Exception e) {
            this.keyMode = oldKeyMode;
            if (getCacheProperty(CACHE_PROP_KEY_MODE) != null) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        /*
         * Parse custom property: ttl-seconds
         */
        long oldTTL = this.timeToLiveSeconds;
        try {
            this.timeToLiveSeconds = Long.parseLong(getCacheProperty(CACHE_PROP_TTL_SECONDS));
        } catch (Exception e) {
            this.timeToLiveSeconds = oldTTL;
            if (getCacheProperty(CACHE_PROP_TTL_SECONDS) != null) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        /*
         * Parse custom property: memcached-hosts-and-ports
         */
        String hostsAndPorts = getCacheProperty(CACHE_PROP_MEMCACHED_HOSTS_AND_PORTS);
        if (!StringUtils.isBlank(hostsAndPorts)) {
            this.memcachedHostsAndPorts = hostsAndPorts;
        }

        if (memcachedClient == null) {
            try {
                memcachedClient = XMemcachedCacheFactory.newMemcachedClient(memcachedHostsAndPorts);
            } catch (IOException e) {
                throw new CacheException(e);
            }
            myOwnMemcachedClient = true;
        }
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize() {
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String key, Object entry) {
        if (entry instanceof CacheEntry) {
            CacheEntry ce = (CacheEntry) entry;
            set(key, ce, ce.getExpireAfterWrite(), ce.getExpireAfterAccess());
        } else {
            set(key, entry, getExpireAfterWrite(), getExpireAfterAccess());
        }
    }

    public final static long TTL_NO_CHANGE = 0;
    public final static long TTL_FOREVER = -1;

    protected String calcCacheKeyNamespace(String key) {
        return getName() + ":" + key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String key, Object entry, long expireAfterWrite, long expireAfterAccess) {
        long ttl = TTL_NO_CHANGE;
        if (!(entry instanceof CacheEntry)) {
            CacheEntry ce = new CacheEntry(key, entry, expireAfterWrite, expireAfterAccess);
            entry = ce;
            ttl = expireAfterAccess > 0 ? expireAfterAccess
                    : (expireAfterWrite > 0 ? expireAfterWrite
                            : (timeToLiveSeconds > 0 ? timeToLiveSeconds : 0));
        } else {
            CacheEntry ce = (CacheEntry) entry;
            ttl = ce.getExpireAfterAccess();
        }
        byte[] _data = serializeCacheEntry((CacheEntry) entry);
        String data = Base64.encodeBase64String(_data);

        final int TTL = (int) ttl;
        switch (keyMode) {
        case MONOPOLISTIC:
        case NAMESPACE: {
            final String KEY = keyMode == KeyMode.MONOPOLISTIC ? key : calcCacheKeyNamespace(key);
            try {
                getMemcachedClient().set(KEY, TTL, data);
            } catch (TimeoutException | InterruptedException | MemcachedException e) {
                throw new CacheException(e);
            }
            break;
        }
        case XNAMESPACE: {
            try {
                getMemcachedClient().withNamespace(getName(), new MemcachedClientCallable<Void>() {
                    @Override
                    public Void call(MemcachedClient client)
                            throws MemcachedException, InterruptedException, TimeoutException {
                        client.set(key, TTL, data);
                        return null;
                    }

                });
            } catch (MemcachedException | InterruptedException | TimeoutException e) {
                throw new CacheException(e);
            }
            break;
        }
        default:
            throw new IllegalStateException("Invalid key mode: " + keyMode);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String key) {
        switch (keyMode) {
        case MONOPOLISTIC:
        case NAMESPACE:
            final String KEY = keyMode == KeyMode.MONOPOLISTIC ? key : calcCacheKeyNamespace(key);
            try {
                getMemcachedClient().deleteWithNoReply(KEY);
            } catch (InterruptedException | MemcachedException e) {
                throw new CacheException(e);
            }
            break;
        case XNAMESPACE:
            try {
                getMemcachedClient().withNamespace(getName(), new MemcachedClientCallable<Void>() {
                    @Override
                    public Void call(MemcachedClient client)
                            throws InterruptedException, MemcachedException {
                        client.deleteWithNoReply(key);
                        return null;
                    }

                });
            } catch (MemcachedException | InterruptedException | TimeoutException e) {
                throw new CacheException(e);
            }
            break;
        default:
            throw new IllegalStateException("Invalid key mode: " + keyMode);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        switch (keyMode) {
        case NAMESPACE:
            throw new CacheException.OperationNotSupportedException(
                    "Key mode[" + keyMode + "] does not support flushall operation.");
        case MONOPOLISTIC:
            try {
                getMemcachedClient().flushAllWithNoReply();
            } catch (InterruptedException | MemcachedException e) {
                throw new CacheException(e);
            }
            break;
        case XNAMESPACE:
            try {
                getMemcachedClient().invalidateNamespace(getName());
            } catch (InterruptedException | MemcachedException | TimeoutException e) {
                throw new CacheException(e);
            }
            break;
        default:
            throw new IllegalStateException("Invalid key mode: " + keyMode);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String key) {
        switch (keyMode) {
        case MONOPOLISTIC:
        case NAMESPACE:
            final String KEY = keyMode == KeyMode.MONOPOLISTIC ? key : calcCacheKeyNamespace(key);
            try {
                return getMemcachedClient().get(KEY) != null;
            } catch (InterruptedException | MemcachedException | TimeoutException e) {
                throw new CacheException(e);
            }
        case XNAMESPACE:
            try {
                return getMemcachedClient().withNamespace(getName(),
                        new MemcachedClientCallable<Boolean>() {
                            @Override
                            public Boolean call(MemcachedClient client) throws InterruptedException,
                                    MemcachedException, TimeoutException {
                                return client.get(key) != null;
                            }

                        });
            } catch (MemcachedException | InterruptedException | TimeoutException e) {
                throw new CacheException(e);
            }
        default:
            throw new IllegalStateException("Invalid key mode: " + keyMode);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object internalGet(String key) {
        String _data = null;
        switch (keyMode) {
        case MONOPOLISTIC:
        case NAMESPACE:
            final String KEY = keyMode == KeyMode.MONOPOLISTIC ? key : calcCacheKeyNamespace(key);
            try {
                _data = getMemcachedClient().get(KEY);
            } catch (TimeoutException | InterruptedException | MemcachedException e) {
                throw new CacheException(e);
            }
            break;
        case XNAMESPACE:
            try {
                _data = getMemcachedClient().withNamespace(getName(),
                        new MemcachedClientCallable<String>() {
                            @Override
                            public String call(MemcachedClient client) throws MemcachedException,
                                    InterruptedException, TimeoutException {
                                return client.get(key);
                            }

                        });
            } catch (MemcachedException | InterruptedException | TimeoutException e) {
                throw new CacheException(e);
            }
            break;
        default:
            throw new IllegalStateException("Invalid key mode: " + keyMode);
        }
        if (_data != null) {
            byte[] data = Base64.decodeBase64(_data);
            CacheEntry ce = deserializeCacheEntry(data);
            if (ce != null && ce.touch()) {
                set(key, ce, ce.getExpireAfterWrite(), ce.getExpireAfterAccess());
            }
            return ce;
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(KeyMode.valueOf("NAMESPACE"));
        System.out.println(KeyMode.valueOf("namespace"));
        System.out.println(KeyMode.valueOf("error"));
    }
}
