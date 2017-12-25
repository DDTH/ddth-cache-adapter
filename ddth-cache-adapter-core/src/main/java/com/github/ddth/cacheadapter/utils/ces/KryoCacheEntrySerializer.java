package com.github.ddth.cacheadapter.utils.ces;

import com.github.ddth.cacheadapter.AbstractCacheEntrySerializer;
import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.ICacheEntrySerializer;
import com.github.ddth.commons.utils.SerializationUtils;

/**
 * This implementation of {@link ICacheEntrySerializer} uses Kryo library
 * (https://github.com/EsotericSoftware/kryo) to serialize/deserialize cache
 * entries.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.3.0
 */
public class KryoCacheEntrySerializer extends AbstractCacheEntrySerializer {

    public final static KryoCacheEntrySerializer instance = new KryoCacheEntrySerializer().init();

    /**
     * {@inheritDoc}
     */
    @Override
    public KryoCacheEntrySerializer init() {
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
        return SerializationUtils.toByteArrayKryo(ce);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CacheEntry doDeserialize(byte[] data) {
        return SerializationUtils.fromByteArrayKryo(data, CacheEntry.class);
    }
}
