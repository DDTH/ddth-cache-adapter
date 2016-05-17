package com.github.ddth.cacheadapter;

/**
 * Interface to compress/decompress data ({@code byte[]}).
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.4.1
 */
public interface ICompressor {
    /**
     * Compresses data.
     * 
     * @param data
     *            @return @throws
     */
    public byte[] compress(byte[] data) throws Exception;

    /**
     * Decompresses data.
     * 
     * @param compressedData
     *            @return @throws
     */
    public byte[] decompress(byte[] compressedData) throws Exception;
}
