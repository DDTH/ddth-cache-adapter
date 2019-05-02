package com.github.ddth.cacheadapter;

/**
 * API interface: load entries into cache.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public interface ICacheLoader {
    /**
     * Load a cache entry.
     * 
     * @param key
     * @return
     * @throws CacheException
     */
    public Object load(String key) throws CacheException;
}
