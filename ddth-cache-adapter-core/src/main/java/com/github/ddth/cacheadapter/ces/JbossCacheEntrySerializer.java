package com.github.ddth.cacheadapter.ces;

import com.github.ddth.cacheadapter.AbstractCacheEntrySerializer;
import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.ICacheEntrySerializer;
import com.github.ddth.commons.utils.SerializationUtils;

/**
 * This implementation of {@link ICacheEntrySerializer} uses Jboss-serialization
 * library (http://serialization.jboss.org) to serialize/deserialize cache
 * entries.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.3.0
 * @deprecated since 0.5.1
 */
public class JbossCacheEntrySerializer extends AbstractCacheEntrySerializer {

    public final static JbossCacheEntrySerializer instance = new JbossCacheEntrySerializer().init();

    /**
     * {@inheritDoc}
     */
    @Override
    public JbossCacheEntrySerializer init() {
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
        return SerializationUtils.toByteArrayJboss(ce);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CacheEntry doDeserialize(byte[] data) {
        return SerializationUtils.fromByteArrayJboss(data, CacheEntry.class);
    }

}
