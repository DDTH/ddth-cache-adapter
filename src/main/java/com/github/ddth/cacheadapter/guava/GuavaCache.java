package com.github.ddth.cacheadapter.guava;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.github.ddth.cacheadapter.AbstractCache;
import com.github.ddth.cacheadapter.AbstractCacheFactory;
import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.CacheException;
import com.github.ddth.cacheadapter.ICacheFactory;
import com.github.ddth.cacheadapter.ICacheLoader;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * In-memory cache, use <a
 * href="http://code.google.com/p/guava-libraries/">Guava</a> as the underlying
 * cache engine.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class GuavaCache extends AbstractCache {

    private LoadingCache<String, Object> cache;

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
    public void init() {
        if (cache != null) {
            return;
        }

        super.init();

        long capacity = getCapacity();
        if (capacity >= 0) {
            if (capacity == 0) {
                capacity = ICacheFactory.DEFAULT_CACHE_CAPACITY;
                setCapacity(capacity);
            }
        }

        int numProcessores = Runtime.getRuntime().availableProcessors();
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder().concurrencyLevel(
                numProcessores * 2);
        if (capacity > 0) {
            cacheBuilder.maximumSize(capacity);
        }

        long expireAfterAccess = getExpireAfterAccess();
        long expireAfterWrite = getExpireAfterWrite();
        if (expireAfterAccess > 0) {
            cacheBuilder.expireAfterAccess(expireAfterAccess, TimeUnit.SECONDS);
        } else if (expireAfterWrite > 0) {
            cacheBuilder.expireAfterWrite(expireAfterWrite, TimeUnit.SECONDS);
        } else {
            cacheBuilder.expireAfterAccess(ICacheFactory.DEFAULT_EXPIRE_AFTER_ACCESS,
                    TimeUnit.SECONDS);
            setExpireAfterAccess(ICacheFactory.DEFAULT_EXPIRE_AFTER_ACCESS);
        }

        CacheLoader<String, Object> guavaCacheLoader = new CacheLoader<String, Object>() {
            @Override
            public Object load(String key) throws Exception {
                ICacheLoader cacheLoader = getCacheLoader();
                Object result = cacheLoader != null ? cacheLoader.load(key) : null;
                if (result != null) {
                    return result;
                } else {
                    throw new CacheException.CacheEntryFoundException();
                }
            }
        };
        cache = cacheBuilder.build(guavaCacheLoader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        try {
            if (cache != null) {
                cache.invalidateAll();
                cache = null;
            }
        } catch (Exception e) {
            // EMPTY
        }

        super.destroy();
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
        cache.put(key, entry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String key, Object entry, long expireAfterWrite, long expireAfterAccess) {
        if (!(entry instanceof CacheEntry)) {
            CacheEntry ce = new CacheEntry(key, entry, expireAfterWrite, expireAfterAccess);
            entry = ce;
        } else {
            CacheEntry ce = (CacheEntry) entry;
            ce.toString();
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
            return cache.get(key);
        } catch (ExecutionException e) {
            Throwable t = e.getCause();
            if (t instanceof CacheException.CacheEntryFoundException) {
                return null;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

}
