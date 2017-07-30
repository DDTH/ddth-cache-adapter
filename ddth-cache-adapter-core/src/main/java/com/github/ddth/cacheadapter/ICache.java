package com.github.ddth.cacheadapter;

import com.github.ddth.cacheadapter.redis.RedisCache;

/**
 * Represents a cache.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public interface ICache {

    public final static byte[] NULL_VALUE = new byte[0];

    /**
     * Get cache's name.
     * 
     * @return
     */
    public String getName();

    /**
     * Get cache's entry loader.
     * 
     * @return
     */
    public ICacheLoader getCacheLoader();

    /**
     * Set cache's entry loader.
     * 
     * @param cacheLoader
     * @return
     */
    public ICache setCacheLoader(ICacheLoader cacheLoader);

    /**
     * Get cache's size (number of current entries).
     * 
     * @return
     */
    public long getSize();

    /**
     * Get cache's capacity (maximum number of entries).
     * 
     * @return
     */
    public long getCapacity();

    /**
     * Return {@code true} if cache supports capacity (i.e. max number of items cache can contain is
     * {@link #getCapacity()}, {@code false} otherwise.
     * 
     * <p>
     * Some cache implementations such as {@link RedisCache} does not support capacity.
     * </p>
     * 
     * @return
     * @since 0.6.2
     */
    public boolean isCapacitySupported();

    /**
     * Get cache's stats.
     * 
     * @return
     */
    public CacheStats getStats();

    /**
     * Get number of seconds before entries to be expired since the last read
     * or write.
     * 
     * @return
     */
    public long getExpireAfterAccess();

    /**
     * Get number of seconds before entries to be expired since the last write.
     * 
     * @return
     */
    public long getExpireAfterWrite();

    /**
     * Put an entry to cache, with default expiry.
     * 
     * @param key
     * @param entry
     */
    public void set(String key, Object entry);

    /**
     * Put an entry to cache, with specified expiries.
     * 
     * @param key
     * @param entry
     * @param expireAfterWrite
     * @param expireAfterAccess
     */
    public void set(String key, Object entry, long expireAfterWrite, long expireAfterAccess);

    /**
     * Get an entry from cache.
     * 
     * @param key
     * @return
     */
    public Object get(String key);

    /**
     * Get an entry from cache. If the entry does not exist in the cache, it
     * will be loaded into cache via the supplied {@link ICacheLoader}.
     * 
     * @param key
     * @param cacheLoader
     * @return
     * @since 0.2.0
     */
    public Object get(String key, ICacheLoader cacheLoader);

    /**
     * Delete an entry from cache.
     * 
     * @param key
     */
    public void delete(String key);

    /**
     * Delete all entries in cache.
     * 
     * @throws CacheException.OperationNotSupportedException
     */
    public void deleteAll() throws CacheException.OperationNotSupportedException;

    /**
     * Check if an entry exists in the cache.
     * 
     * @param key
     * @return
     */
    public boolean exists(String key);
}
