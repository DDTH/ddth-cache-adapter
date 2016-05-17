package com.github.ddth.cacheadapter.ces;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.InflaterOutputStream;

import com.github.ddth.cacheadapter.ICompressor;

/**
 * This compressor utilizes JDK {@link InflaterInputStream}/
 * {@link DeflaterOutputStream} to compress/decompress data.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.4.1
 */
public class JdkDeflateCompressor implements ICompressor {

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
     * 
     * @throws IOException
     */
    @Override
    public byte[] compress(byte[] data) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Deflater def = new Deflater(compressionLevel, false);
            try (DeflaterOutputStream dos = new DeflaterOutputStream(baos, def)) {
                dos.write(data);
                dos.finish();
                return baos.toByteArray();
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @throws IOException
     */
    @Override
    public byte[] decompress(byte[] compressedData) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (InflaterOutputStream ios = new InflaterOutputStream(baos)) {
                ios.write(compressedData);
                ios.finish();
                return baos.toByteArray();
            }
        }
    }

}
