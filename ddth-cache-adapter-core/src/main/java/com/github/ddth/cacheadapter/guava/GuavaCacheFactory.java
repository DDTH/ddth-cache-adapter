package com.github.ddth.cacheadapter.guava;

import com.github.ddth.cacheadapter.AbstractCache;
import com.github.ddth.cacheadapter.AbstractCacheFactory;
import com.github.ddth.cacheadapter.ICacheFactory;

/**
 * <a href="http://code.google.com/p/guava-libraries/">Guava</a> implementation
 * of {@link ICacheFactory}.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class GuavaCacheFactory extends AbstractCacheFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractCache createCacheInternal(String name, long capacity, long expireAfterWrite,
            long expireAfterAccess) {
        GuavaCache cache = new GuavaCache();
        cache.setName(name).setCapacity(capacity).setExpireAfterAccess(expireAfterAccess)
                .setExpireAfterWrite(expireAfterWrite);
        return cache;
    }

}
