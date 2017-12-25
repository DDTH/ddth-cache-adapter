package com.github.ddth.cacheadapter.cacheimpl.localremote;

import com.github.ddth.cacheadapter.AbstractCache;
import com.github.ddth.cacheadapter.CacheException;
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

    public LocalRemoteCache(String name, ICache localCache, ICache remoteCache) {
        this(name, null, localCache, remoteCache);
    }

    public LocalRemoteCache(String name, LocalRemoteCacheFactory cacheFactory, ICache localCache,
            ICache remoteCache) {
        super(name, cacheFactory);
        this.localCache = localCache;
        this.remoteCache = remoteCache;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCapacitySupported() {
        return localCache.isCapacitySupported() && remoteCache.isCapacitySupported();
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
        try {
            if (localCache != null) {
                localCache.set(key, entry);
            }
            if (remoteCache != null) {
                remoteCache.set(key, entry);
            }
        } catch (Exception e) {
            throw e instanceof CacheException ? (CacheException) e : new CacheException(e);
        }
    }

    /**
     * Puts an entry to both local and remote caches, with specified expiries.
     */
    @Override
    public void set(String key, Object entry, long expireAfterWrite, long expireAfterAccess) {
        try {
            if (localCache != null) {
                localCache.set(key, entry, expireAfterWrite, expireAfterAccess);
            }
            if (remoteCache != null) {
                remoteCache.set(key, entry, expireAfterWrite, expireAfterAccess);
            }
        } catch (Exception e) {
            throw e instanceof CacheException ? (CacheException) e : new CacheException(e);
        }
    }

    /**
     * Delete an entry from both local and remote caches.
     */
    @Override
    public void delete(String key) {
        try {
            if (localCache != null) {
                localCache.delete(key);
            }
            if (remoteCache != null) {
                remoteCache.delete(key);
            }
        } catch (Exception e) {
            throw e instanceof CacheException ? (CacheException) e : new CacheException(e);
        }
    }

    /**
     * Delete an entry from the local cache.
     * 
     * @param key
     */
    public void deleteLocal(String key) {
        if (localCache != null) {
            try {
                localCache.delete(key);
            } catch (Exception e) {
                throw e instanceof CacheException ? (CacheException) e : new CacheException(e);
            }
        }
    }

    /**
     * Deletes all entries in both local and remote caches.
     */
    @Override
    public void deleteAll() {
        try {
            if (localCache != null) {
                localCache.deleteAll();
            }
            if (remoteCache != null) {
                remoteCache.deleteAll();
            }
        } catch (Exception e) {
            throw e instanceof CacheException ? (CacheException) e : new CacheException(e);
        }
    }

    /**
     * Deletes all entries in the local cache.
     */
    public void deleteAllLocal() {
        if (localCache != null) {
            try {
                localCache.deleteAll();
            } catch (Exception e) {
                throw e instanceof CacheException ? (CacheException) e : new CacheException(e);
            }
        }
    }

    /**
     * Checks if an entry exists in either local or remote cache.
     */
    @Override
    public boolean exists(String key) {
        try {
            return (localCache != null && localCache.exists(key))
                    || (remoteCache != null && remoteCache.exists(key));
        } catch (Exception e) {
            throw e instanceof CacheException ? (CacheException) e : new CacheException(e);
        }
    }

    /**
     * Checks if an entry exists in the local cache.
     * 
     * @param key
     */
    public boolean existsLocal(String key) {
        try {
            return localCache != null && localCache.exists(key);
        } catch (Exception e) {
            throw e instanceof CacheException ? (CacheException) e : new CacheException(e);
        }
    }

    /**
     * Checks if an entry exists in the local cache.
     * 
     * @param key
     */
    public boolean existsRemote(String key) {
        try {
            return remoteCache != null && remoteCache.exists(key);
        } catch (Exception e) {
            throw e instanceof CacheException ? (CacheException) e : new CacheException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object internalGet(String key) {
        try {
            Object value = localCache != null ? localCache.get(key) : null;
            if (value == null) {
                value = remoteCache != null ? remoteCache.get(key) : null;
                if (value != null && localCache != null) {
                    localCache.set(key, value);
                }
            }
            return value;
        } catch (Exception e) {
            throw e instanceof CacheException ? (CacheException) e : new CacheException(e);
        }
    }

}
