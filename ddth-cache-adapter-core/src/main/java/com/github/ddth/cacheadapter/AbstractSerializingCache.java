package com.github.ddth.cacheadapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ddth.cacheadapter.utils.ces.DefaultCacheEntrySerializer;

/**
 * Abstract cache implementation that requires cache entries to be
 * serialized/de-serialized.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.3.0
 */
public abstract class AbstractSerializingCache extends AbstractCache
        implements ICacheEntrySerializer {

    private Logger LOGGER = LoggerFactory.getLogger(AbstractSerializingCache.class);

    /**
     * Used to serialize/deserialize cache entries.
     */
    private ICacheEntrySerializer cacheEntrySerializer;

    public AbstractSerializingCache() {
    }

    public AbstractSerializingCache(String name, AbstractCacheFactory cacheFactory) {
        super(name, cacheFactory);
    }

    public AbstractSerializingCache(String name, AbstractCacheFactory cacheFactory,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory);
        this.cacheEntrySerializer = cacheEntrySerializer;
    }

    public AbstractSerializingCache(String name, AbstractCacheFactory cacheFactory, long capacity) {
        super(name, cacheFactory, capacity);
    }

    public AbstractSerializingCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory, capacity);
        this.cacheEntrySerializer = cacheEntrySerializer;
    }

    public AbstractSerializingCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess);
    }

    public AbstractSerializingCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess);
        this.cacheEntrySerializer = cacheEntrySerializer;
    }

    public AbstractSerializingCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess, ICacheLoader cacheLoader) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess, cacheLoader);
    }

    public AbstractSerializingCache(String name, AbstractCacheFactory cacheFactory, long capacity,
            long expireAfterWrite, long expireAfterAccess, ICacheLoader cacheLoader,
            ICacheEntrySerializer cacheEntrySerializer) {
        super(name, cacheFactory, capacity, expireAfterWrite, expireAfterAccess, cacheLoader);
        this.cacheEntrySerializer = cacheEntrySerializer;
    }

    protected ICacheEntrySerializer getCacheEntrySerializer() {
        return cacheEntrySerializer;
    }

    public AbstractCache setCacheEntrySerializer(ICacheEntrySerializer cacheEntrySerializer) {
        this.cacheEntrySerializer = cacheEntrySerializer;
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.6.4
     */
    @Override
    public byte[] serialize(CacheEntry ce) {
        try {
            return ce != null && cacheEntrySerializer != null ? cacheEntrySerializer.serialize(ce)
                    : null;
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.6.4
     */
    @Override
    public CacheEntry deserialize(byte[] data) {
        try {
            return data != null && cacheEntrySerializer != null
                    ? cacheEntrySerializer.deserialize(data)
                    : null;
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return null;
        }
    }

    /**
     * @deprecated use {@link #serialize(CacheEntry)} instead.
     */
    protected byte[] serializeCacheEntry(CacheEntry ce) {
        try {
            return ce != null && cacheEntrySerializer != null ? cacheEntrySerializer.serialize(ce)
                    : null;
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return null;
        }
    }

    /**
     * @deprecated use {@link #deserialize(byte[])} instead.
     */
    protected CacheEntry deserializeCacheEntry(byte[] data) {
        try {
            return data != null && cacheEntrySerializer != null
                    ? cacheEntrySerializer.deserialize(data)
                    : null;
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractSerializingCache init() {
        super.init();
        if (cacheEntrySerializer == null) {
            cacheEntrySerializer = DefaultCacheEntrySerializer.instance;
        }
        return this;
    }
}
