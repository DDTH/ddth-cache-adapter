package com.github.ddth.cacheadapter.utils;

import java.nio.ByteBuffer;

import org.apache.thrift.TException;

import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.thrift.TCacheEntry;
import com.github.ddth.commons.utils.SerializationUtils;

/**
 * Thrift utility class for internal use.
 * 
 * @author ThanhNB
 * @since 0.3.2
 */
public class ThriftUtils {

    public static TCacheEntry newCacheEntry(CacheEntry ce) {
        if (ce == null) {
            return null;
        }
        Object value = ce.getValueSilent();
        byte[] valueData = SerializationUtils.toByteArray(value);
        String valueClass = valueData != null ? value.getClass().getName() : null;
        return new TCacheEntry(ce.getKey(), valueData != null ? ByteBuffer.wrap(valueData) : null,
                valueClass, ce.getCreationTimestamp(), ce.getLastAccessTimestamp(),
                ce.getExpireAfterWrite(), ce.getExpireAfterAccess());
    }

    public static byte[] serialize(CacheEntry ce) throws TException {
        TCacheEntry cacheEntry = newCacheEntry(ce);
        return com.github.ddth.commons.utils.ThriftUtils.toBytes(cacheEntry);
    }

    public static CacheEntry deserialize(byte[] data) throws TException {
        TCacheEntry cacheEntry = com.github.ddth.commons.utils.ThriftUtils.fromBytes(data,
                TCacheEntry.class);
        if (cacheEntry == null) {
            return null;
        }
        try {
            CacheEntry ce = new CacheEntry();
            ce.setExpireAfterAccess(cacheEntry.expireAfterAccess)
                    .setExpireAfterWrite(cacheEntry.expireAfterWrite).setKey(cacheEntry.key);
            ce.setCreationTimestamp(cacheEntry.creationTimestampMs)
                    .setLastAccessTimestamp(cacheEntry.lastAccessTimestampMs);
            if (cacheEntry.valueClass != null) {
                Class<?> clazz = (Class<?>) Class.forName(cacheEntry.valueClass);
                ce.setValue(SerializationUtils.fromByteArray(cacheEntry.getValue(), clazz));
            }
            return ce;
        } catch (ClassNotFoundException e) {
            throw new TException(e);
        }
    }
}
