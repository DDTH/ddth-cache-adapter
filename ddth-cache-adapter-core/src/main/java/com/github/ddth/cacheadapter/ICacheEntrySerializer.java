package com.github.ddth.cacheadapter;

/**
 * Serialize/Deserialize cache entry to/from {@code byte[]}
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.2.2
 */
public interface ICacheEntrySerializer {
    /**
     * Serializes an object to {@code byte[]}.
     * 
     * @param ce
     * @return
     */
    public byte[] serialize(CacheEntry ce);

    /**
     * Deserializes a {@code byte[]} to object.
     * 
     * @param data
     * @return
     */
    public CacheEntry deserialize(byte[] data);
}
