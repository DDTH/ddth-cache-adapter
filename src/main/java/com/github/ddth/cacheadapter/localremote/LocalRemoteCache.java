package com.github.ddth.cacheadapter.localremote;

import com.github.ddth.cacheadapter.AbstractCache;
import com.github.ddth.cacheadapter.AbstractCacheFactory;
import com.github.ddth.cacheadapter.ICache;

/**
 * A compound cache. Item is first looked up from "local" cache, then "remote"
 * cache if "local" cache is a miss.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.2.0
 */
public class LocalRemoteCache extends AbstractCache {

    private ICache localCache, remoteCache;

    public LocalRemoteCache() {
    }

    public LocalRemoteCache(String name, AbstractCacheFactory cacheFactory) {
        super(name, cacheFactory);
    }

    public LocalRemoteCache(String name, AbstractCacheFactory cacheFactory, ICache localCache,
            ICache remoteCache) {
        super(name, cacheFactory);
        this.localCache = localCache;
        this.remoteCache = remoteCache;
    }

    public ICache getLocalCache() {
        return localCache;
    }

    public LocalRemoteCache setLocalCache(ICache localCache) {
        this.localCache = localCache;
        return this;
    }

    public ICache getRemoteCache() {
        return remoteCache;
    }

    public LocalRemoteCache setRemoteCache(ICache remoteCache) {
        this.remoteCache = remoteCache;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize() {
        return -1;
    }

    /**
     * Puts an entry to both local and remote caches, with default expiry.
     */
    @Override
    public void set(String key, Object entry) {
        if (localCache != null) {
            localCache.set(key, entry);
        }
        if (remoteCache != null) {
            remoteCache.set(key, entry);
        }
    }

    /**
     * Puts an entry to both local and remote caches, with specified expiries.
     */
    @Override
    public void set(String key, Object entry, long expireAfterWrite, long expireAfterAccess) {
        if (localCache != null) {
            localCache.set(key, entry, expireAfterWrite, expireAfterAccess);
        }
        if (remoteCache != null) {
            remoteCache.set(key, entry, expireAfterWrite, expireAfterAccess);
        }
    }

    /**
     * Delete an entry from both local and remote caches.
     */
    @Override
    public void delete(String key) {
        if (localCache != null) {
            localCache.delete(key);
        }
        if (remoteCache != null) {
            remoteCache.delete(key);
        }
    }

    /**
     * Delete an entry from the local cache.
     * 
     * @param key
     */
    public void deleteLocal(String key) {
        if (localCache != null) {
            localCache.delete(key);
        }
    }

    /**
     * Deletes all entries in both local and remote caches.
     */
    @Override
    public void deleteAll() {
        if (localCache != null) {
            localCache.deleteAll();
        }
        if (remoteCache != null) {
            remoteCache.deleteAll();
        }
    }

    /**
     * Deletes all entries in the local cache.
     */
    public void deleteAllLocal() {
        if (localCache != null) {
            localCache.deleteAll();
        }
    }

    /**
     * Checks if an entry exists in either local or remote cache.
     */
    @Override
    public boolean exists(String key) {
        return (localCache != null && localCache.exists(key))
                || (remoteCache != null && remoteCache.exists(key));
    }

    /**
     * Checks if an entry exists in the local cache.
     * 
     * @param key
     */
    public boolean existsLocal(String key) {
        return localCache != null && localCache.exists(key);
    }

    /**
     * Checks if an entry exists in the local cache.
     * 
     * @param key
     */
    public boolean existsRemote(String key) {
        return remoteCache != null && remoteCache.exists(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object internalGet(String key) {
        Object value = localCache != null ? localCache.get(key) : null;
        if (value == null) {
            value = remoteCache != null ? remoteCache.get(key) : null;
            if (value != null && localCache != null) {
                localCache.set(key, value);
            }
        }
        return value;
    }

}
