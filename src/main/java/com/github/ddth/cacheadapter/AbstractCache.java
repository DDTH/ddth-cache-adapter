package com.github.ddth.cacheadapter;

/**
 * Abstract implementation of {@link ICache}.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public abstract class AbstractCache implements ICache {

    private String name;
    private long capacity;
    private long expireAfterWrite;
    private long expireAfterAccess;
    private CacheStats stats = new CacheStats();
    private ICacheLoader cacheLoader;

    public AbstractCache() {
    }

    public AbstractCache(String name) {
        this.name = name;
    }

    public AbstractCache(String name, long capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public AbstractCache(String name, long capacity, long expireAfterWrite, long expireAfterAccess) {
        this.name = name;
        this.capacity = capacity;
        this.expireAfterWrite = expireAfterWrite;
        this.expireAfterAccess = expireAfterAccess;
    }

    public AbstractCache(String name, long capacity, long expireAfterWrite, long expireAfterAccess,
            ICacheLoader cacheLoader) {
        this.name = name;
        this.capacity = capacity;
        this.expireAfterWrite = expireAfterWrite;
        this.expireAfterAccess = expireAfterAccess;
        this.cacheLoader = cacheLoader;
    }

    public void init() {

    }

    public void destroy() {

    }

    public void inactive() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    public AbstractCache setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICacheLoader getCacheLoader() {
        return cacheLoader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractCache setCacheLoader(ICacheLoader cacheLoader) {
        this.cacheLoader = cacheLoader;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract long getSize();

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCapacity() {
        return capacity;
    }

    public AbstractCache setCapacity(long capacity) {
        this.capacity = capacity;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CacheStats getStats() {
        return stats;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getExpireAfterWrite() {
        return expireAfterWrite;
    }

    public AbstractCache setExpireAfterWrite(long expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getExpireAfterAccess() {
        return expireAfterAccess;
    }

    public AbstractCache setExpireAfterAccess(long expireAfterAccess) {
        this.expireAfterAccess = expireAfterAccess;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void set(String key, Object entry);

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void set(String key, Object entry, long expireAfterWrite, long expireAfterAccess);

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void delete(String key);

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void deleteAll();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract boolean exists(String key);

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(String key) {
        Object value = internalGet(key);
        if (value instanceof CacheEntry) {
            if (((CacheEntry) value).isExpired()) {
                stats.miss();
                return null;
            } else {
                // update entry's access timestamp
                ((CacheEntry) value).touch();
                set(key, value);
            }
        }
        if (value == null) {
            stats.miss();
        } else {
            stats.hit();
        }
        return value;
    }

    /**
     * Gets an entry from cache. Sub-class overrides this method to actually
     * retrieve entries from cache.
     * 
     * @param key
     * @return
     */
    protected abstract Object internalGet(String key);
}
