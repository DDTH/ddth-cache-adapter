package com.github.ddth.cacheadapter.utils.compressor;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.InflaterOutputStream;

import com.github.ddth.cacheadapter.CacheException;
import com.github.ddth.cacheadapter.ICompressor;

/**
 * This compressor utilizes JDK {@link InflaterInputStream}/
 * {@link DeflaterOutputStream} to compress/decompress data.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.4.1
 */
public class JdkDeflateCompressor implements ICompressor {

    public final static JdkDeflateCompressor instance = new JdkDeflateCompressor().init();

    private int compressionLevel = 1;

    public JdkDeflateCompressor() {
    }

    public JdkDeflateCompressor(int compressionLevel) {
        setCompressionLevel(compressionLevel);
    }

    public int getCompressionLevel() {
        return compressionLevel;
    }

    public JdkDeflateCompressor setCompressionLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel >= 1 && compressionLevel <= 9 ? compressionLevel
                : 6;
        return this;
    }

    public JdkDeflateCompressor init() {
        return this;
    }

    public void destroy() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] compress(byte[] data) throws CacheException {
        try {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                Deflater def = new Deflater(compressionLevel, false);
                try (DeflaterOutputStream dos = new DeflaterOutputStream(baos, def)) {
                    dos.write(data);
                    dos.finish();
                    return baos.toByteArray();
                }
            }
        } catch (Exception e) {
            throw e instanceof CacheException ? (CacheException) e : new CacheException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] decompress(byte[] compressedData) throws CacheException {
        try {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                try (InflaterOutputStream ios = new InflaterOutputStream(baos)) {
                    ios.write(compressedData);
                    ios.finish();
                    return baos.toByteArray();
                }
            }
        } catch (Exception e) {
            throw e instanceof CacheException ? (CacheException) e : new CacheException(e);
        }
    }
}
