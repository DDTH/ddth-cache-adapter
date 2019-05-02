package com.github.ddth.cacheadapter;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * Abstract implementation of {@link ICacheFactory} that creates
 * {@link AbstractCache} instances.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public abstract class AbstractCacheFactory implements ICacheFactory, AutoCloseable {

    public final static String CACHE_PROP_CAPACITY = "cache.capacity";
    public final static String CACHE_PROP_EXPIRE_AFTER_WRITE = "cache.expireAfterWrite";
    public final static String CACHE_PROP_EXPIRE_AFTER_ACCESS = "cache.expireAfterAccess";

    private long defaultCacheCapacity = ICacheFactory.DEFAULT_CACHE_CAPACITY;
    private long defaultExpireAfterAccess = ICacheFactory.DEFAULT_EXPIRE_AFTER_ACCESS;
    private long defaultExpireAfterWrite = ICacheFactory.DEFAULT_EXPIRE_AFTER_WRITE;

    private String cacheNamePrefix;

    private Map<String, Properties> cacheProperties;
    private Cache<String, AbstractCache> cacheInstances = CacheBuilder.newBuilder()
            .expireAfterAccess(3600, TimeUnit.SECONDS)
            .removalListener(new RemovalListener<String, AbstractCache>() {
                @Override
                public void onRemoval(RemovalNotification<String, AbstractCache> notification) {
                    AbstractCache cache = notification.getValue();
                    cache.destroy();
                }
            }).build();

    public AbstractCacheFactory() {
    }

    public AbstractCacheFactory init() {
        return this;
    }

    public void destroy() {
        cacheInstances.invalidateAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        destroy();
    }

    /**
     * Prefix {@link #cacheNamePrefix} to {@code cacheName} if
     * {@link #cacheNamePrefix} is not null.
     * 
     * @param cacheName
     * @return
     */
    protected String buildCacheName(String cacheName) {
        return cacheNamePrefix != null ? cacheNamePrefix + cacheName : cacheName;
    }

    public String getCacheNamePrefix() {
        return cacheNamePrefix;
    }

    /**
     * Name of caches created by this factory will be prefixed by this string.
     * 
     * @param cacheNamePrefix
     * @return
     */
    public AbstractCacheFactory setCacheNamePrefix(String cacheNamePrefix) {
        this.cacheNamePrefix = cacheNamePrefix;
        return this;
    }

    public long getDefaultCacheCapacity() {
        return defaultCacheCapacity;
    }

    public AbstractCacheFactory setDefaultCacheCapacity(long defaultCacheCapacity) {
        this.defaultCacheCapacity = defaultCacheCapacity;
        return this;
    }

    public long getDefaultExpireAfterAccess() {
        return defaultExpireAfterAccess;
    }

    public AbstractCacheFactory setDefaultExpireAfterAccess(long defaultExpireAfterAccess) {
        this.defaultExpireAfterAccess = defaultExpireAfterAccess;
        return this;
    }

    public long getDefaultExpireAfterWrite() {
        return defaultExpireAfterWrite;
    }

    public AbstractCacheFactory setDefaultExpireAfterWrite(long defaultExpireAfterWrite) {
        this.defaultExpireAfterWrite = defaultExpireAfterWrite;
        return this;
    }

    public AbstractCacheFactory setCacheProperties(Map<String, Properties> cacheProperties) {
        this.cacheProperties = cacheProperties;
        return this;
    }

    /**
     * Get all cache properties settings.
     * 
     * @return
     * @since 0.2.0.2
     */
    protected Map<String, Properties> getCachePropertiesMap() {
        return cacheProperties;
    }

    /**
     * Get a cache's properties
     * 
     * @param name
     * @return
     */
    protected Properties getCacheProperties(String name) {
        return cacheProperties != null ? cacheProperties.get(name) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractCache createCache(String name) {
        return createCache(name, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractCache createCache(String name, ICacheLoader cacheLoader) {
        return createCache(name, defaultCacheCapacity, defaultExpireAfterWrite,
                defaultExpireAfterAccess, cacheLoader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractCache createCache(String name, long capacity) {
        return createCache(name, capacity, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractCache createCache(String name, long capacity, ICacheLoader cacheLoader) {
        return createCache(name, capacity, defaultExpireAfterWrite, defaultExpireAfterAccess,
                cacheLoader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractCache createCache(String name, long capacity, long expireAfterWrite,
            long expireAfterAccess) {
        return createCache(name, capacity, expireAfterWrite, expireAfterAccess, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractCache createCache(String name, long capacity, long expireAfterWrite,
            long expireAfterAccess, ICacheLoader cacheLoader) {
        String cacheName = buildCacheName(name);
        try {
            AbstractCache cache = cacheInstances.get(cacheName, new Callable<AbstractCache>() {
                @Override
                public AbstractCache call() throws Exception {
                    // check if custom cache settings exist
                    long cacheCapacity = capacity;
                    long cacheExpireAfterWrite = expireAfterWrite;
                    long cacheExpireAfterAccess = expireAfterAccess;

                    // yup, use "name" here (not "cacheName) is correct and
                    // intended!
                    Properties cacheProps = getCacheProperties(name);
                    if (cacheProps != null) {
                        try {
                            cacheCapacity = Long
                                    .parseLong(cacheProps.getProperty(CACHE_PROP_CAPACITY));
                        } catch (Exception e) {
                            cacheCapacity = capacity;
                        }
                        try {
                            cacheExpireAfterWrite = Long.parseLong(
                                    cacheProps.getProperty(CACHE_PROP_EXPIRE_AFTER_WRITE));
                        } catch (Exception e) {
                            cacheExpireAfterWrite = expireAfterWrite;
                        }
                        try {
                            cacheExpireAfterAccess = Long.parseLong(
                                    cacheProps.getProperty(CACHE_PROP_EXPIRE_AFTER_ACCESS));
                        } catch (Exception e) {
                            cacheExpireAfterAccess = expireAfterAccess;
                        }
                    }
                    return createAndInitCacheInstance(cacheName, cacheCapacity,
                            cacheExpireAfterWrite, cacheExpireAfterAccess, cacheLoader, cacheProps);
                }
            });
            return cache;
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create and initializes an {@link ICache} instance, ready for use.
     * 
     * @param name
     * @param capacity
     * @param expireAfterWrite
     * @param expireAfterAccess
     * @param cacheLoader
     * @param cacheProps
     * @return
     */
    protected AbstractCache createAndInitCacheInstance(String name, long capacity,
            long expireAfterWrite, long expireAfterAccess, ICacheLoader cacheLoader,
            Properties cacheProps) {
        AbstractCache cache = createCacheInternal(name, capacity, expireAfterWrite,
                expireAfterAccess, cacheProps);
        cache.setCacheLoader(cacheLoader).setCacheFactory(this);
        cache.init();
        return cache;
    }

    /**
     * Create a new cache instance, but does not initialize it. Convenient
     * method for sub-class to override.
     * 
     * @param name
     * @param capacity
     * @param expireAfterWrite
     * @param expireAfterAccess
     * @param cacheProps
     * @return
     */
    protected abstract AbstractCache createCacheInternal(String name, long capacity,
            long expireAfterWrite, long expireAfterAccess, Properties cacheProps);

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeCache(String name) {
        String cacheName = buildCacheName(name);
        cacheInstances.invalidate(cacheName);
    }
}
