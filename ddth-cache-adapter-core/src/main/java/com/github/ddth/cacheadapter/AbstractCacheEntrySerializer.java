package com.github.ddth.cacheadapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract implementation of {@link ICacheEntrySerializer}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.3.2
 */
public abstract class AbstractCacheEntrySerializer implements ICacheEntrySerializer {

    private final Logger LOGGER = LoggerFactory.getLogger(AbstractCacheEntrySerializer.class);
    private ICompressor compressor;

    public AbstractCacheEntrySerializer init() {
        return this;
    }

    public void destroy() {
    }

    /**
     * 
     * @return
     * @since 0.4.1
     */
    public ICompressor getCompressor() {
        return compressor;
    }

    /**
     * 
     * @param compressor
     * @return
     * @since 0.4.1
     */
    public AbstractCacheEntrySerializer setCompressor(ICompressor compressor) {
        this.compressor = compressor;
        return this;
    }

    /**
     * Sub-class should implement this method.
     * 
     * @param ce
     * @return
     * @since 0.4.1
     * @throws Exception
     */
    protected abstract byte[] doSerialize(CacheEntry ce) throws Exception;

    /**
     * Sub-class should implement this method.
     * 
     * @param data
     * @return
     * @since 0.4.1
     * @throws Exception
     */
    protected abstract CacheEntry doDeserialize(byte[] data) throws Exception;

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] serialize(CacheEntry ce) {
        try {
            byte[] data = doSerialize(ce);
            return data != null ? (compressor != null ? compressor.compress(data) : data) : null;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CacheEntry deserialize(byte[] _data) {
        try {
            byte[] data = _data != null
                    ? (compressor != null ? compressor.decompress(_data) : _data) : null;
            return data != null ? doDeserialize(data) : null;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }
}
