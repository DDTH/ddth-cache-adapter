package com.github.ddth.cacheadapter;

/**
 * Represents a cache.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public interface ICache {

    public final static byte[] NULL_VALUE = new byte[0];

    /**
     * Gets cache's name.
     * 
     * @return
     */
    public String getName();

    /**
     * Gets cache's entry loader.
     * 
     * @return
     */
    public ICacheLoader getCacheLoader();

    /**
     * Sets cache's entry loader.
     * 
     * @param cacheLoader
     * @return
     */
    public ICache setCacheLoader(ICacheLoader cacheLoader);

    /**
     * Gets cache's size (number of current entries).
     * 
     * @return
     */
    public long getSize();

    /**
     * Gets cache's capacity (maximum number of entries).
     * 
     * @return
     */
    public long getCapacity();

    /**
     * Gets cache's stats.
     * 
     * @return
     */
    public CacheStats getStats();

    /**
     * Gets number of seconds before entries to be expired since the last read
     * or write.
     * 
     * @return
     */
    public long getExpireAfterAccess();

    /**
     * Gets number of seconds before entries to be expired since the last write.
     * 
     * @return
     */
    public long getExpireAfterWrite();

    /**
     * Puts an entry to cache, with default expiry.
     * 
     * @param key
     * @param entry
     */
    public void set(String key, Object entry);

    /**
     * Puts an entry to cache, with specified expiries.
     * 
     * @param key
     * @param entry
     * @param expireAfterWrite
     * @param expireAfterAccess
     */
    public void set(String key, Object entry, long expireAfterWrite, long expireAfterAccess);

    /**
     * Gets an entry from cache.
     * 
     * @param key
     * @return
     */
    public Object get(String key);

    /**
     * Deletes an entry from cache.
     * 
     * @param key
     */
    public void delete(String key);

    /**
     * Deletes all entries in cache.
     */
    public void deleteAll();

    /**
     * Checks if an entry exists in the cache.
     * 
     * @param key
     * @return
     */
    public boolean exists(String key);
}
