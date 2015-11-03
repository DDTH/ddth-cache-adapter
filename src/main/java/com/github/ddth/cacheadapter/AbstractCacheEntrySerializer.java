package com.github.ddth.cacheadapter;

/**
 * Abstract implementation of {@link ICacheEntrySerializer}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.3.2
 */
public abstract class AbstractCacheEntrySerializer implements ICacheEntrySerializer {

    public AbstractCacheEntrySerializer init() {
        return this;
    }

    public void destroy() {
        // EMPTY
    }

}
