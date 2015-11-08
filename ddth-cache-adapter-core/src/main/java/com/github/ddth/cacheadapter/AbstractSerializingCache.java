package com.github.ddth.cacheadapter;

/**
 * Abstract cache implementation that requires cache entries to be
 * serilized/deserialized.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.3.0
 */
public abstract class AbstractSerializingCache extends AbstractCache {

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

    protected byte[] serializeCacheEntry(CacheEntry ce) {
        return ce != null ? cacheEntrySerializer.serialize(ce) : null;
    }

    protected CacheEntry deserializeCacheEntry(byte[] data) {
        return data != null ? cacheEntrySerializer.deserialize(data) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        super.init();
        if (cacheEntrySerializer == null) {
            cacheEntrySerializer = new DefaultCacheEntrySerializer();
        }
    }
}
