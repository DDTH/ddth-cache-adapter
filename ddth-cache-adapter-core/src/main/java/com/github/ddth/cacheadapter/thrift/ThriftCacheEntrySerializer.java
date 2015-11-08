package com.github.ddth.cacheadapter.thrift;

import org.apache.thrift.TException;

import com.github.ddth.cacheadapter.AbstractCacheEntrySerializer;
import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.ICacheEntrySerializer;
import com.github.ddth.cacheadapter.utils.ThriftUtils;

/**
 * This implementation of {@link ICacheEntrySerializer} uses Apache Thrift to
 * serializes/deserializes cache entries.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.3.2
 */
public class ThriftCacheEntrySerializer extends AbstractCacheEntrySerializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public ThriftCacheEntrySerializer init() {
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
    public byte[] serialize(final CacheEntry ce) {
        try {
            return ThriftUtils.serialize(ce);
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CacheEntry deserialize(final byte[] data) {
        try {
            return ThriftUtils.deserialize(data);
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }

}
