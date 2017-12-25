package com.github.ddth.cacheadapter.utils.ces;

import com.github.ddth.cacheadapter.AbstractCacheEntrySerializer;
import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.ICacheEntrySerializer;
import com.github.ddth.commons.utils.SerializationUtils;

/**
 * This implementation of {@link ICacheEntrySerializer} uses FST library
 * (https://github.com/RuedigerMoeller/fast-serialization) to
 * serialize/deserialize cache entries.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.5.1
 */
public class FstCacheEntrySerializer extends AbstractCacheEntrySerializer {

    public final static FstCacheEntrySerializer instance = new FstCacheEntrySerializer().init();

    /**
     * {@inheritDoc}
     */
    @Override
    public FstCacheEntrySerializer init() {
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
        return SerializationUtils.toByteArrayFst(ce);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CacheEntry doDeserialize(byte[] data) {
        return SerializationUtils.fromByteArrayFst(data, CacheEntry.class);
    }

}
