package com.github.ddth.cacheadapter.cacheimpl.localremote;

import java.util.Properties;

import com.github.ddth.cacheadapter.AbstractCache;
import com.github.ddth.cacheadapter.AbstractCacheFactory;
import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.ICacheFactory;

/**
 * Local-remote implementation of {@link ICacheFactory}. Item is first looked up
 * from "local" cache, then "remote" cache if "local" cache is a miss.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.2.0
 */
public class LocalRemoteCacheFactory extends AbstractCacheFactory {

    private ICacheFactory localCacheFactory, remoteCacheFactory;

    public ICacheFactory getLocalCacheFactory() {
        return localCacheFactory;
    }

    public LocalRemoteCacheFactory setLocalCacheFactory(ICacheFactory localCacheFactory) {
        this.localCacheFactory = localCacheFactory;
        return this;
    }

    public ICacheFactory getRemoteCacheFactory() {
        return remoteCacheFactory;
    }

    public LocalRemoteCacheFactory setRemoteCacheFactory(ICacheFactory remoteCacheFactory) {
        this.remoteCacheFactory = remoteCacheFactory;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractCache createCacheInternal(String name, long capacity, long expireAfterWrite,
            long expireAfterAccess, Properties cacheProps) {
        ICache localCache = localCacheFactory != null
                ? localCacheFactory.createCache(name, capacity, expireAfterWrite, expireAfterAccess)
                : null;
        ICache remoteCache = remoteCacheFactory != null ? remoteCacheFactory.createCache(name,
                capacity, expireAfterWrite, expireAfterAccess) : null;
        LocalRemoteCache cache = new LocalRemoteCache();
        cache.setName(name).setCapacity(capacity).setExpireAfterAccess(expireAfterAccess)
                .setExpireAfterWrite(expireAfterWrite).setCacheProperties(cacheProps);
        cache.setLocalCache(localCache).setRemoteCache(remoteCache);
        return cache;
    }
}
