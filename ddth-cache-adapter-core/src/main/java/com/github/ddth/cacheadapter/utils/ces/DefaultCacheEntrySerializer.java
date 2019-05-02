package com.github.ddth.cacheadapter.utils.ces;

import com.github.ddth.cacheadapter.AbstractCacheEntrySerializer;
import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.ICacheEntrySerializer;
import com.github.ddth.commons.serialization.FstSerDeser;
import com.github.ddth.commons.serialization.ISerDeser;

/**
 * This implementation of {@link ICacheEntrySerializer} uses a {@link ISerDeser}
 * instance (default {@link FstSerDeser} for serialization/deserialization.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.5.0
 */
public class DefaultCacheEntrySerializer extends AbstractCacheEntrySerializer {

    public final static DefaultCacheEntrySerializer instance = new DefaultCacheEntrySerializer()
            .init();

    /**
     * Used for object serialization/deserialization.
     * 
     * @since 0.6.4
     */
    private ISerDeser serDeser;

    /**
     * Get the associated {@link ISerDeser} instance.
     * 
     * @return
     * @since 0.6.4
     */
    public ISerDeser getSerDeser() {
        return serDeser;
    }

    /**
     * Associate a {@link ISerDeser} instance with this cache-entry-serializer.
     * 
     * @param serDeser
     * @return
     * @since 0.6.4
     */
    public DefaultCacheEntrySerializer setSerDeser(ISerDeser serDeser) {
        this.serDeser = serDeser;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultCacheEntrySerializer init() {
        super.init();
        if (serDeser == null) {
            serDeser = new FstSerDeser();
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        super.destroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected byte[] doSerialize(CacheEntry ce) {
        return serDeser.toBytes(ce);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CacheEntry doDeserialize(byte[] data) {
        return serDeser.fromBytes(data, CacheEntry.class);
    }
}
