package com.github.ddth.cacheadapter.ces;

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
     * 
     * @throws TException
     */
    @Override
    protected byte[] doSerialize(CacheEntry ce) throws TException {
        return ThriftUtils.serialize(ce);
    }

    /**
     * {@inheritDoc}
     * 
     * @throws TException
     */
    @Override
    protected CacheEntry doDeserialize(byte[] data) throws TException {
        return ThriftUtils.deserialize(data);
    }

}
