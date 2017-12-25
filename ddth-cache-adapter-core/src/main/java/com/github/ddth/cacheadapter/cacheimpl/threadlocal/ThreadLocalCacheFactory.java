package com.github.ddth.cacheadapter.cacheimpl.threadlocal;

import java.util.Properties;

import com.github.ddth.cacheadapter.AbstractCache;
import com.github.ddth.cacheadapter.AbstractCacheFactory;
import com.github.ddth.cacheadapter.ICacheFactory;
import com.github.ddth.cacheadapter.cacheimpl.guava.GuavaCacheFactory;

/**
 * In-memory threadlocal-scope implementation of {@link ICacheFactory}.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.2.0
 */
public class ThreadLocalCacheFactory extends AbstractCacheFactory {

    private final ThreadLocal<ICacheFactory> caches = new ThreadLocal<ICacheFactory>() {
        @Override
        protected ICacheFactory initialValue() {
            GuavaCacheFactory cf = new GuavaCacheFactory();
            cf.setCacheNamePrefix(getCacheNamePrefix());
            cf.setCacheProperties(getCachePropertiesMap());
            cf.setDefaultCacheCapacity(getDefaultCacheCapacity());
            cf.setDefaultExpireAfterAccess(getDefaultExpireAfterAccess());
            cf.setDefaultExpireAfterWrite(getDefaultExpireAfterWrite());
            cf.init();
            return cf;
        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractCache createCacheInternal(String name, long capacity, long expireAfterWrite,
            long expireAfterAccess, Properties cacheProps) {
        ICacheFactory cacheFactory = caches.get();
        return (AbstractCache) cacheFactory.createCache(name);
    }

}
