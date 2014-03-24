package com.github.ddth.cacheadapter;

/**
 * Loads entries to cache.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public interface ICacheLoader {
    /**
     * Loads a cache entry.
     * 
     * @param key
     * @return
     * @throws Exception
     */
    public Object load(String key) throws Exception;
}
