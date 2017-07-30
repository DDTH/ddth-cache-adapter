package com.github.ddth.cacheadapter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.github.ddth.cacheadapter.utils.CacheUtils;
import com.github.ddth.commons.serialization.DeserializationException;
import com.github.ddth.commons.serialization.ISerializationSupport;
import com.github.ddth.commons.serialization.SerializationException;
import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.commons.utils.SerializationUtils;

/**
 * Encapsulates a cache item with extra functionality.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class CacheEntry implements Serializable, Cloneable, ISerializationSupport {

    private final static long serialVersionUID = "0.5.1".hashCode();
    private final static long LAST_ACCESS_THRESHOLD_MS = 1000;

    private String key = "";
    private Object value = ArrayUtils.EMPTY_BYTE_ARRAY;

    private long creationTimestampMs = System.currentTimeMillis();
    private long lastAccessTimestampMs = System.currentTimeMillis();
    private long expireAfterWrite = -1;
    private long expireAfterAccess = -1;

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

    public CacheEntry() {
    }

    public CacheEntry(String key, Object value) {
        setKey(key);
        setValue(value);
    }

    public CacheEntry(String key, Object value, long expireAfterWrite, long expireAfterAccess) {
        setKey(key);
        setValue(value);
        setExpireAfterAccess(expireAfterAccess);
        setExpireAfterWrite(expireAfterWrite);
    }

    public boolean isExpired() {
        long now = System.currentTimeMillis();
        return expireAfterAccess > 0 ? lastAccessTimestampMs + expireAfterAccess * 1000L < now
                : expireAfterWrite > 0 ? creationTimestampMs + expireAfterWrite * 1000L < now
                        : false;
    }

    public String getKey() {
        return key;
    }

    public CacheEntry setKey(String key) {
        this.key = key;
        return this;
    }

    /**
     * Gets the wrapped value silently (do not check for expiry or update last
     * access timestamp).
     * 
     * @return
     */
    public Object getValueSilent() {
        return value;
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

    public CacheEntry setCreationTimestamp(long creationTimestampMs) {
        this.creationTimestampMs = creationTimestampMs;
        return this;
    }

    public long getLastAccessTimestamp() {
        return lastAccessTimestampMs;
    }

    public CacheEntry setLastAccessTimestamp(long lastAccessTimestampMs) {
        this.lastAccessTimestampMs = lastAccessTimestampMs;
        return this;
    }

    /**
     * "Touch" the cache entry.
     * 
     * @return
     * @since 0.2.1 entry can be touched only if {@code expireAfterAccess > 0}.
     */
    public boolean touch() {
        if (expireAfterAccess > 0) {
            long now = System.currentTimeMillis();
            if (lastAccessTimestampMs + LAST_ACCESS_THRESHOLD_MS < now) {
                lastAccessTimestampMs = now;
                return true;
            }
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
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
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
        if (obj == this) {
            return true;
        }
        if (obj instanceof CacheEntry) {
            CacheEntry other = (CacheEntry) obj;
            EqualsBuilder eq = new EqualsBuilder();
            eq.append(this.key, other.key).append(this.value, other.value)
                    .append(this.creationTimestampMs, other.creationTimestampMs)
                    .append(this.expireAfterAccess, other.expireAfterAccess)
                    .append(this.expireAfterWrite, other.expireAfterWrite);
            return eq.isEquals();
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.2.2
     */
    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder(19, 81);
        hcb.append(this.key).append(this.value).append(this.creationTimestampMs)
                .append(this.expireAfterAccess).append(this.expireAfterWrite);
        return hcb.hashCode();
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.5.0
     */
    @Override
    public byte[] toBytes() throws SerializationException {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("k", this.key);
        dataMap.put("tac", this.lastAccessTimestampMs);
        dataMap.put("tcr", this.creationTimestampMs);
        dataMap.put("eac", this.expireAfterAccess);
        dataMap.put("ewr", this.expireAfterWrite);

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (value != null) {
            dataMap.put("_c_", value.getClass().getName());
            dataMap.put("v", SerializationUtils.toByteArray(this.value, cl));
        }

        return SerializationUtils.toByteArrayFst(dataMap, cl);
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.5.0
     */
    @SuppressWarnings("unchecked")
    @Override
    public CacheEntry fromBytes(byte[] data) throws DeserializationException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Map<String, Object> dataMap = SerializationUtils.fromByteArrayFst(data, Map.class, cl);

        this.key = DPathUtils.getValue(dataMap, "k", String.class);

        Long tAccess = DPathUtils.getValue(dataMap, "tac", Long.class);
        this.lastAccessTimestampMs = tAccess != null ? tAccess.longValue()
                : System.currentTimeMillis();

        Long tCreate = DPathUtils.getValue(dataMap, "tcr", Long.class);
        this.creationTimestampMs = tCreate != null ? tCreate.longValue()
                : System.currentTimeMillis();

        Long eAccess = DPathUtils.getValue(dataMap, "eac", Long.class);
        this.expireAfterAccess = eAccess != null ? eAccess.longValue() : -1;

        Long eWrite = DPathUtils.getValue(dataMap, "ewr", Long.class);
        this.expireAfterWrite = eWrite != null ? eWrite.longValue() : -1;

        String clazzName = DPathUtils.getValue(dataMap, "_c_", String.class);
        if (!StringUtils.isBlank(clazzName)) {
            try {
                byte[] valueData = DPathUtils.getValue(dataMap, "v", byte[].class);
                Class<?> clazz = Class.forName(clazzName, true, cl);
                this.value = SerializationUtils.fromByteArray(valueData, clazz, cl);
            } catch (ClassNotFoundException e) {
                throw new DeserializationException(e);
            }
        }

        return this;
    }
}
