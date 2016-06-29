package com.github.ddth.cacheadapter.guava;

import java.lang.reflect.Method;
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
 * In-memory cache, use
 * <a href="http://code.google.com/p/guava-libraries/">Guava</a> as the
 * underlying cache engine.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class GuavaCache extends AbstractCache {

    private LoadingCache<String, Object> cache;
    private boolean cloneCacheEntries = false;

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
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder()
                .concurrencyLevel(numProcessores * 2);
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
                    throw new CacheException.CacheEntryNotFoundException();
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
     * If {@code true}, cache entries are cloned before putting into cache,
     * default value is {@code false}.
     * 
     * @return
     * @since 0.4.1.1
     */
    public boolean isCloneCacheEntries() {
        return cloneCacheEntries;
    }

    /**
     * If {@code true}, cache entries are cloned before putting into cache,
     * default value is {@code false}.
     * 
     * @return
     * @since 0.4.1.1
     */
    public boolean getCloneCacheEntries() {
        return cloneCacheEntries;
    }

    /**
     * If {@code true}, cache entries are cloned before putting into cache.
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
     * Try cloning an object.
     * 
     * @param _obj
     * @return
     * @since 0.5.0.6
     */
    private Object tryCloneObject(Object _obj) {
        if (_obj == null) {
            return null;
        }
        Object obj = _obj instanceof CacheEntry ? obj = ((CacheEntry) _obj).getValue() : _obj;
        Object clonedObj = null;
        if (obj instanceof Cloneable) {
            try {
                Method method = Object.class.getDeclaredMethod("clone");
                method.setAccessible(true);
                clonedObj = method.invoke(obj);
            } catch (Exception e) {
                if (e instanceof CloneNotSupportedException) {
                    clonedObj = obj;
                } else {
                    throw e instanceof RuntimeException ? (RuntimeException) e
                            : new RuntimeException(e);
                }
            }
        } else {
            clonedObj = obj;
        }
        if (_obj instanceof CacheEntry) {
            ((CacheEntry) _obj).setValue(clonedObj);
            clonedObj = _obj;
        }
        return clonedObj;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String key, Object entry) {
        cache.put(key, cloneCacheEntries ? tryCloneObject(entry) : entry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String key, Object _entry, long expireAfterWrite, long expireAfterAccess) {
        Object entry = cloneCacheEntries ? tryCloneObject(_entry) : _entry;
        if (!(entry instanceof CacheEntry)) {
            CacheEntry ce = new CacheEntry(key, entry, expireAfterWrite, expireAfterAccess);
            entry = ce;
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
            return result;
        } catch (Exception e) {
            Throwable t = e.getCause();
            if (t instanceof CacheException.CacheEntryNotFoundException) {
                return null;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

}
