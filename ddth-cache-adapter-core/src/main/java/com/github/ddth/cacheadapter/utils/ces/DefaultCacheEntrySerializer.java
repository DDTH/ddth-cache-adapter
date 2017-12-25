package com.github.ddth.cacheadapter.utils.ces;

import com.github.ddth.cacheadapter.AbstractCacheEntrySerializer;
import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.ICacheEntrySerializer;
import com.github.ddth.commons.utils.SerializationUtils;

/**
 * This implementation of {@link ICacheEntrySerializer} uses
 * {@link SerializationUtils#toByteArray(Object)} and
 * {@link SerializationUtils#fromByteArray(byte[], Class)} for
 * serializing/deserializing.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.5.0
 */
public class DefaultCacheEntrySerializer extends AbstractCacheEntrySerializer {

    public final static DefaultCacheEntrySerializer instance = new DefaultCacheEntrySerializer()
            .init();

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultCacheEntrySerializer init() {
        super.init();
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
        return SerializationUtils.toByteArray(ce);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CacheEntry doDeserialize(byte[] data) {
        return SerializationUtils.fromByteArray(data, CacheEntry.class);
    }

}
