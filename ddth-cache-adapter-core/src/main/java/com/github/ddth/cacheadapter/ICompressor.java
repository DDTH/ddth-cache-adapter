package com.github.ddth.cacheadapter;

/**
 * API interface: compress/decompress data ({@code byte[]}).
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.4.1
 */
public interface ICompressor {
    /**
     * Compress data.
     * 
     * @param data
     * @return @throws
     */
    public byte[] compress(byte[] data) throws CacheException;

    /**
     * Decompress data.
     * 
     * @param compressedData
     * @return @throws
     */
    public byte[] decompress(byte[] compressedData) throws CacheException;
}
