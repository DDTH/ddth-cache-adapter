package com.github.ddth.cacheadapter.threadlocal;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.github.ddth.cacheadapter.AbstractCache;
import com.github.ddth.cacheadapter.AbstractCacheFactory;
import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.CacheException;
import com.github.ddth.cacheadapter.ICacheLoader;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * In-memory threadlocal-scope cache.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.2.0
 */
public class ThreadLocalCache extends AbstractCache {

    private static ThreadLocal<Cache<String, Object>> cache = new ThreadLocal<Cache<String, Object>>() {
        @Override
        protected Cache<String, Object> initialValue() {
            Cache<String, Object> cache = CacheBuilder.newBuilder().concurrencyLevel(1)
                    .expireAfterWrite(60, TimeUnit.SECONDS).build();
            return cache;
        }
    };

    public ThreadLocalCache() {
    }

    public ThreadLocalCache(String name, AbstractCacheFactory cacheFactory) {
        super(name, cacheFactory);
    }

    public ThreadLocalCache(String name, AbstractCacheFactory cacheFactory, long capacity) {
        super(name, cacheFactory, capacity);
    }

    public ThreadLocalCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess);
    }

    public ThreadLocalCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess, ICacheLoader cacheLoader) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess, cacheLoader);
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
        cache.get().put(key, entry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String key, Object entry, long expireAfterWrite, long expireAfterAccess) {
        if (!(entry instanceof CacheEntry)) {
            CacheEntry ce = new CacheEntry(key, entry, expireAfterWrite, expireAfterAccess);
            entry = ce;
        }
        cache.get().put(key, entry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String key) {
        cache.get().invalidate(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        cache.get().invalidateAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String key) {
        return cache.get().getIfPresent(key) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object internalGet(String key) {
        try {
            return cache.get().get(key, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    throw new CacheException.CacheEntryFoundException();
                }
            });
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
