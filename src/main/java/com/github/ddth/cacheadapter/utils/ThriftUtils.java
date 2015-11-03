package com.github.ddth.cacheadapter.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import org.apache.commons.io.IOUtils;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;

import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.thrift.TCacheEntry;

/**
 * Thrift utility class for internal use.
 * 
 * @author ThanhNB
 * @since 0.3.2
 */
public class ThriftUtils {

    private static TProtocolFactory protocolFactory = new TCompactProtocol.Factory();

    public static TCacheEntry newCacheEntry(CacheEntry ce) {
        if (ce == null) {
            return null;
        }
        byte[] valueData = KryoUtils.serialize(ce.getValue());
        String valueClass = valueData != null ? ce.getValue().getClass().getName() : null;
        return new TCacheEntry(ce.getKey(), valueData != null ? ByteBuffer.wrap(valueData) : null,
                valueClass, ce.getCreationTimestamp(), ce.getLastAccessTimestamp(),
                ce.getExpireAfterWrite(), ce.getExpireAfterAccess());
    }

    public static byte[] serialize(CacheEntry ce) throws TException {
        TCacheEntry cacheEntry = newCacheEntry(ce);
        return toBytes(cacheEntry);
    }

    @SuppressWarnings("unchecked")
    public static CacheEntry deserialize(byte[] data) throws TException {
        TCacheEntry cacheEntry = fromBytes(data, TCacheEntry.class);
        if (cacheEntry == null) {
            return null;
        }
        try {
            CacheEntry ce = new CacheEntry();
            ce.setExpireAfterAccess(cacheEntry.expireAfterAccess)
                    .setExpireAfterWrite(cacheEntry.expireAfterWrite).setKey(cacheEntry.key);
            Class<CacheEntry> clazz = (Class<CacheEntry>) Class.forName(cacheEntry.valueClass);
            ce.setValue(KryoUtils.deserialize(cacheEntry.getValue(), clazz));
            return ce;
        } catch (ClassNotFoundException e) {
            throw new TException(e);
        }
    }

    /**
     * Serializes a thrift object to byte array.
     * 
     * @param record
     * @return
     * @throws TException
     */
    public static byte[] toBytes(TBase<?, ?> record) throws TException {
        if (record == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            TTransport transport = new TIOStreamTransport(null, baos);
            TProtocol oProtocol = protocolFactory.getProtocol(transport);
            record.write(oProtocol);
            return baos.toByteArray();
        } finally {
            IOUtils.closeQuietly(baos);
        }
    }

    /**
     * Deserializes a thrift object from byte array.
     * 
     * @param data
     * @param clazz
     * @return
     * @throws TException
     */
    public static <T extends TBase<?, ?>> T fromBytes(byte[] data, Class<T> clazz)
            throws TException {
        if (data == null) {
            return null;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        try {
            TTransport transport = new TIOStreamTransport(bais, null);
            TProtocol iProtocol = protocolFactory.getProtocol(transport);
            T record = clazz.newInstance();
            record.read(iProtocol);
            return record;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new TException(e);
        } finally {
            IOUtils.closeQuietly(bais);
        }
    }
}
