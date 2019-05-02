package com.github.ddth.cacheadapter.cacheimpl.guava;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ddth.cacheadapter.AbstractCache;
import com.github.ddth.cacheadapter.AbstractCacheFactory;
import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.CacheException;
import com.github.ddth.cacheadapter.ICacheLoader;
import com.github.ddth.cacheadapter.utils.CacheUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * In-memory cache, use
 * <a href="http://code.google.com/p/guava-libraries/">Guava</a> as the
 * underlying cache engine.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class GuavaCache extends AbstractCache {

    private final Logger LOGGER = LoggerFactory.getLogger(GuavaCache.class);

    /**
     * To override the {@link #setCloneCacheEntries(boolean)} setting.
     * 
     * @since 0.6.1
     */
    public final static String CACHE_PROP_CLONE_CACHE_ENTRIES = "cache.clone_cache_entries";

    private LoadingCache<String, Object> cache;
    private boolean cloneCacheEntries = true;

    public GuavaCache() {
    }

    public GuavaCache(String name, AbstractCacheFactory cacheFactory) {
        super(name, cacheFactory);
    }

    public GuavaCache(String name, AbstractCacheFactory cacheFactory, long capacity) {
        super(name, cacheFactory, capacity);
    }

    public GuavaCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess);
    }

    public GuavaCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess, ICacheLoader cacheLoader) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess, cacheLoader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GuavaCache init() {
        super.init();

        /*
         * Parse custom property: clone-cache-entries
         */
        boolean oldCloneCacheEntries = this.cloneCacheEntries;
        try {
            this.cloneCacheEntries = Boolean
                    .parseBoolean(getCacheProperty(CACHE_PROP_CLONE_CACHE_ENTRIES));
        } catch (Exception e) {
            this.cloneCacheEntries = oldCloneCacheEntries;
            if (getCacheProperty(CACHE_PROP_CLONE_CACHE_ENTRIES) != null) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        int numProcessores = Runtime.getRuntime().availableProcessors();
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder()
                .concurrencyLevel(numProcessores * 2);
        if (getCapacity() > 0) {
            cacheBuilder.maximumSize(getCapacity());
        }

        long expireAfterAccess = getExpireAfterAccess();
        long expireAfterWrite = getExpireAfterWrite();
        if (expireAfterAccess > 0) {
            cacheBuilder.expireAfterAccess(expireAfterAccess, TimeUnit.SECONDS);
        } else if (expireAfterWrite > 0) {
            cacheBuilder.expireAfterWrite(expireAfterWrite, TimeUnit.SECONDS);
        }

        CacheLoader<String, Object> guavaCacheLoader = new CacheLoader<String, Object>() {
            @Override
            public Object load(String key) throws Exception {
                ICacheLoader cacheLoader = getCacheLoader();
                Object result = cacheLoader != null ? cacheLoader.load(key) : null;
                if (result != null) {
                    return result;
                } else {
                    throw new CacheException.CacheEntryNotFoundException();
                }
            }
        };
        cache = cacheBuilder.build(guavaCacheLoader);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        try {
            super.destroy();
        } finally {
            try {
                if (cache != null) {
                    cache.invalidateAll();
                    cache = null;
                }
            } catch (Exception e) {
                // EMPTY
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCapacitySupported() {
        return true;
    }

    /**
     * If {@code true}, cache entries are cloned when fetching out of cache,
     * default value is {@code false}.
     * 
     * @return
     * @since 0.4.1.1
     */
    public boolean isCloneCacheEntries() {
        return cloneCacheEntries;
    }

    /**
     * If {@code true}, cache entries are cloned when fetching out of cache,
     * default value is {@code true}.
     * 
     * @return
     * @since 0.4.1.1
     */
    public boolean getCloneCacheEntries() {
        return cloneCacheEntries;
    }

    /**
     * If {@code true}, cache entries are cloned when fetching out of cache.
     * 
     * @param cloneCacheEntries
     * @return
     * @since 0.4.1.1
     */
    public GuavaCache setCloneCacheEntries(boolean cloneCacheEntries) {
        this.cloneCacheEntries = cloneCacheEntries;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize() {
        return cache.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String key, Object entry) {
        cache.put(key, CacheUtils.tryClone(entry));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String key, Object entry, long expireAfterWrite, long expireAfterAccess) {
        if (!(entry instanceof CacheEntry)) {
            CacheEntry ce = new CacheEntry(key, CacheUtils.tryClone(entry), expireAfterWrite,
                    expireAfterAccess);
            entry = ce;
        } else {
            entry = CacheUtils.tryClone(entry);
        }
        cache.put(key, entry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String key) {
        cache.invalidate(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        cache.invalidateAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String key) {
        return cache.getIfPresent(key) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object internalGet(String key) {
        try {
            Object result = cache.get(key);
            if (result instanceof CacheEntry) {
                // update entry's private TTL if needed.
                CacheEntry ce = (CacheEntry) result;
                if (ce.touch()) {
                    set(key, ce);
                }
            }
            return cloneCacheEntries ? CacheUtils.tryClone(result) : result;
        } catch (Exception e) {
            Throwable t = e.getCause();
            if (t instanceof CacheException.CacheEntryNotFoundException) {
                return null;
            } else {
                throw t instanceof CacheException ? (CacheException) t : new CacheException(t);
            }
        }
    }
}
