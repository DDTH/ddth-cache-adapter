package com.github.ddth.cacheadapter;

import java.io.Serializable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.github.ddth.cacheadapter.utils.CacheUtils;

/**
 * Encapsulates a cache item with extra functionality.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class CacheEntry implements Serializable, Cloneable {

    private static final long serialVersionUID = "0.4.1.3".hashCode();

    private String key = "";
    private Object value = ArrayUtils.EMPTY_BYTE_ARRAY;
    private long creationTimestampMs = System.currentTimeMillis(),
            lastAccessTimestampMs = System.currentTimeMillis(), expireAfterWrite = -1,
            expireAfterAccess = -1;

    /**
     * {@inheritDoc}
     */
    public CacheEntry clone() {
        try {
            CacheEntry clone = (CacheEntry) super.clone();
            clone.value = CacheUtils.tryClone(value);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    private void _init() {
        creationTimestampMs = System.currentTimeMillis();
        lastAccessTimestampMs = System.currentTimeMillis();
        expireAfterWrite = -1;
        expireAfterAccess = -1;
    }

    public CacheEntry() {
        _init();
    }

    public CacheEntry(String key, Object value) {
        _init();
        setKey(key);
        setValue(value);
    }

    public CacheEntry(String key, Object value, long expireAfterWrite, long expireAfterAccess) {
        _init();
        setKey(key);
        setValue(value);
        setExpireAfterAccess(expireAfterAccess);
        setExpireAfterWrite(expireAfterWrite);
    }

    public boolean isExpired() {
        if (expireAfterWrite > 0) {
            return creationTimestampMs + expireAfterWrite * 1000L <= System.currentTimeMillis();
        }
        if (expireAfterAccess > 0) {
            return lastAccessTimestampMs + expireAfterAccess * 1000L <= System.currentTimeMillis();
        }
        return false;
    }

    public String getKey() {
        return key;
    }

    public CacheEntry setKey(String key) {
        this.key = key;
        return this;
    }

    public Object getValue() {
        if (!isExpired()) {
            touch();
            return value;
        }
        return null;
    }

    public CacheEntry setValue(Object value) {
        this.value = value;
        return this;
    }

    public long getExpireAfterWrite() {
        return expireAfterWrite;
    }

    public CacheEntry setExpireAfterWrite(long expireAfterWriteSeconds) {
        this.expireAfterWrite = expireAfterWriteSeconds;
        return this;
    }

    public long getExpireAfterAccess() {
        return expireAfterAccess;
    }

    public CacheEntry setExpireAfterAccess(long expireAfterAccessSeconds) {
        this.expireAfterAccess = expireAfterAccessSeconds;
        return this;
    }

    public long getCreationTimestamp() {
        return creationTimestampMs;
    }

    public long getLastAccessTimestamp() {
        return lastAccessTimestampMs;
    }

    /**
     * "Touch" the cache entry.
     * 
     * @return
     * @since 0.2.1 entry can be touched only if {@code expireAfterAccess >0}.
     */
    public boolean touch() {
        if (expireAfterAccess > 0) {
            lastAccessTimestampMs = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.2.2
     */
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append("key", this.key);
        tsb.append("value", this.value);
        tsb.append("timestampCreate", this.creationTimestampMs);
        tsb.append("timestampLastAccess", this.lastAccessTimestampMs);
        tsb.append("expireAfterAccess", this.expireAfterAccess);
        tsb.append("expireAfterWrite", this.expireAfterWrite);
        return tsb.toString();
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.2.2
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CacheEntry)) {
            return false;
        }
        CacheEntry other = (CacheEntry) obj;
        EqualsBuilder eq = new EqualsBuilder();
        eq.append(this.key, other.key).append(this.value, other.value);
        // .append(this.creationTimestampMs, other.creationTimestampMs)
        // .append(this.lastAccessTimestampMs, other.lastAccessTimestampMs)
        // .append(this.expireAfterAccess, other.expireAfterAccess)
        // .append(this.expireAfterWrite, other.expireAfterWrite);
        return eq.isEquals();
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.2.2
     */
    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder(19, 81);
        hcb.append(this.key).append(this.value);
        return hcb.hashCode();
    }
}
