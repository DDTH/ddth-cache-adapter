package com.github.ddth.cacheadapter;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract implementation of {@link ICache}.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public abstract class AbstractCache implements ICache, AutoCloseable {

    private final Logger LOGGER = LoggerFactory.getLogger(AbstractCache.class);

    /**
     * To override the {@link #setCapacity(long)} setting.
     * 
     * @since 0.6.2
     */
    public final static String CACHE_PROP_CAPACITY = "cache.capacity";

    /**
     * To override the {@link #setExpireAfterAccess(long)} setting.
     * 
     * @since 0.6.2
     */
    public final static String CACHE_PROP_EXPIRE_AFTER_ACCESS = "cache.expire_after_access";

    /**
     * To override the {@link #setExpireAfterWrite(long)} setting.
     * 
     * @since 0.6.2
     */
    public final static String CACHE_PROP_EXPIRE_AFTER_WRITE = "cache.expire_after_write";

    private String name;
    private long capacity;
    private long expireAfterWrite;
    private long expireAfterAccess;
    private CacheStats stats = new CacheStats();
    private ICacheLoader cacheLoader;
    private AbstractCacheFactory cacheFactory;
    private Properties cacheProps;

    public AbstractCache() {
    }

    public AbstractCache(String name, AbstractCacheFactory cacheFactory) {
        this.name = name;
        this.cacheFactory = cacheFactory;
    }

    public AbstractCache(String name, AbstractCacheFactory cacheFactory, long capacity) {
        this.name = name;
        this.cacheFactory = cacheFactory;
        this.capacity = capacity;
    }

    public AbstractCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess) {
        this.name = name;
        this.cacheFactory = cacheFactory;
        this.capacity = capacity;
        this.expireAfterWrite = expireAfterWrite;
        this.expireAfterAccess = expireAfterAccess;
    }

    public AbstractCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess, ICacheLoader cacheLoader) {
        this.name = name;
        this.cacheFactory = cacheFactory;
        this.capacity = capacity;
        this.expireAfterWrite = expireAfterWrite;
        this.expireAfterAccess = expireAfterAccess;
        this.cacheLoader = cacheLoader;
    }

    /**
     * Initializes the cache before use.
     */
    public AbstractCache init() {
        /*
         * Parse custom property: capacity
         */
        long oldCapacity = this.capacity;
        try {
            String entry = getCacheProperty(CACHE_PROP_CAPACITY);
            if (entry != null) {
                this.capacity = Long.parseLong(entry);
            }
        } catch (Exception e) {
            this.capacity = oldCapacity;
            LOGGER.warn(e.getMessage(), e);
        }
        if (capacity < -1) {
            setCapacity(-1);
        }

        /*
         * Parse custom property: expire-after-access
         */
        long oldExpireAfterAccess = this.expireAfterAccess;
        try {
            String entry = getCacheProperty(CACHE_PROP_EXPIRE_AFTER_ACCESS);
            if (entry != null) {
                this.expireAfterAccess = Long.parseLong(entry);
            }
        } catch (Exception e) {
            this.expireAfterAccess = oldExpireAfterAccess;
            LOGGER.warn(e.getMessage(), e);
        }
        if (expireAfterAccess < -1) {
            setExpireAfterAccess(-1);
        }

        /*
         * Parse custom property: expire-after-write
         */
        long oldExpireAfterWrite = this.expireAfterWrite;
        try {
            String entry = getCacheProperty(CACHE_PROP_EXPIRE_AFTER_WRITE);
            if (entry != null) {
                this.expireAfterWrite = Long.parseLong(entry);
            }
        } catch (Exception e) {
            this.expireAfterWrite = oldExpireAfterWrite;
            LOGGER.warn(e.getMessage(), e);
        }
        if (expireAfterWrite < -1) {
            setExpireAfterWrite(-1);
        }

        return this;
    }

    /**
     * Destroys the cache after use.
     */
    public void destroy() {
    }
    
    /**
     * {@inheritDoc}
     * @since 0.6.3.3
     */
    @Override
    public void close() {
        destroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    public AbstractCache setName(String name) {
        this.name = name;
        return this;
    }

    protected AbstractCacheFactory getCacheFactory() {
        return cacheFactory;
    }

    public AbstractCache setCacheFactory(AbstractCacheFactory cacheFactory) {
        this.cacheFactory = cacheFactory;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICacheLoader getCacheLoader() {
        return cacheLoader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractCache setCacheLoader(ICacheLoader cacheLoader) {
        this.cacheLoader = cacheLoader;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract long getSize();

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCapacity() {
        return capacity;
    }

    public AbstractCache setCapacity(long capacity) {
        this.capacity = capacity;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CacheStats getStats() {
        return stats;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getExpireAfterWrite() {
        return expireAfterWrite;
    }

    public AbstractCache setExpireAfterWrite(long expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getExpireAfterAccess() {
        return expireAfterAccess;
    }

    public AbstractCache setExpireAfterAccess(long expireAfterAccess) {
        this.expireAfterAccess = expireAfterAccess;
        return this;
    }

    /**
     * Cache's custom properties.
     * 
     * @param cacheProps
     * @return
     * @since 0.6.1
     */
    public AbstractCache setCacheProperties(Properties cacheProps) {
        this.cacheProps = cacheProps != null ? new Properties(cacheProps) : new Properties();
        return this;
    }

    /**
     * Get cache's custom properties.
     * 
     * @return
     * @since 0.6.1
     */
    protected Properties getCacheProperties() {
        return cacheProps;
    }

    /**
     * Get cache's custom property.
     * 
     * @param key
     * @return
     * @since 0.6.1
     */
    protected String getCacheProperty(String key) {
        return cacheProps != null ? cacheProps.getProperty(key) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void set(String key, Object entry);

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void set(String key, Object entry, long expireAfterWrite,
            long expireAfterAccess);

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void delete(String key);

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void deleteAll();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract boolean exists(String key);

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(String key) {
        return get(key, cacheLoader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(String key, ICacheLoader cacheLoader) {
        Object value = internalGet(key);
        boolean fromCacheLoader = false;
        if (value == null && cacheLoader != null) {
            try {
                value = cacheLoader.load(key);
                fromCacheLoader = true;
            } catch (Exception e) {
                throw e instanceof CacheException ? (CacheException) e : new CacheException(e);
            }
        }
        if (value instanceof CacheEntry) {
            CacheEntry ce = (CacheEntry) value;
            value = ce.getValue();
        }
        if (fromCacheLoader || value == null) {
            stats.miss();
        } else {
            stats.hit();
        }
        return value;
    }

    /**
     * Gets an entry from cache. Sub-class overrides this method to actually
     * retrieve entries from cache.
     * 
     * @param key
     * @return
     */
    protected abstract Object internalGet(String key);
}
