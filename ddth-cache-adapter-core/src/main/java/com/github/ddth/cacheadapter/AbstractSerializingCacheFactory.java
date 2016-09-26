package com.github.ddth.cacheadapter;

import com.github.ddth.cacheadapter.ces.KryoCacheEntrySerializer;

/**
 * Abstract implementation of {@link ICacheFactory} that creates
 * {@link AbstractSerializingCache} instances.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.3.0
 */
public abstract class AbstractSerializingCacheFactory extends AbstractCacheFactory {

    private ICacheEntrySerializer cacheEntrySerializer;

    public AbstractSerializingCacheFactory() {
    }

    protected ICacheEntrySerializer getCacheEntrySerializer() {
        return cacheEntrySerializer;
    }

    public AbstractSerializingCacheFactory setCacheEntrySerializer(
            ICacheEntrySerializer cacheEntrySerializer) {
        this.cacheEntrySerializer = cacheEntrySerializer;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractSerializingCache createCache(String name) {
        return (AbstractSerializingCache) super.createCache(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractSerializingCache createCache(String name, ICacheLoader cacheLoader) {
        return (AbstractSerializingCache) super.createCache(name, cacheLoader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractSerializingCache createCache(String name, long capacity) {
        return (AbstractSerializingCache) super.createCache(name, capacity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractSerializingCache createCache(String name, long capacity,
            ICacheLoader cacheLoader) {
        return (AbstractSerializingCache) super.createCache(name, capacity, cacheLoader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractSerializingCache createCache(String name, long capacity, long expireAfterWrite,
            long expireAfterAccess) {
        return (AbstractSerializingCache) super.createCache(name, capacity, expireAfterWrite,
                expireAfterAccess);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractSerializingCache createCache(final String name, final long capacity,
            final long expireAfterWrite, final long expireAfterAccess,
            final ICacheLoader cacheLoader) {
        return (AbstractSerializingCache) super.createCache(name, capacity, expireAfterWrite,
                expireAfterAccess, cacheLoader);
    }

    /**
     * Creates and initializes an {@link ICache} instance, ready for use.
     * 
     * @param name
     * @param capacity
     * @param expireAfterWrite
     * @param expireAfterAccess
     * @param cacheLoader
     * @return
     */
    protected AbstractSerializingCache createAndInitCacheInstance(String name, long capacity,
            long expireAfterWrite, long expireAfterAccess, ICacheLoader cacheLoader) {
        AbstractSerializingCache cache = createCacheInternal(name, capacity, expireAfterWrite,
                expireAfterAccess);
        cache.setCacheEntrySerializer(cacheEntrySerializer).setCacheLoader(cacheLoader)
                .setCacheFactory(this);
        cache.init();
        return cache;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected abstract AbstractSerializingCache createCacheInternal(String name, long capacity,
            long expireAfterWrite, long expireAfterAccess);

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractSerializingCacheFactory init() {
        super.init();
        if (cacheEntrySerializer == null) {
            cacheEntrySerializer = KryoCacheEntrySerializer.instance;
        }
        return this;
    }
}
