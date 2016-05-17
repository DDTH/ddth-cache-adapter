package com.github.ddth.cacheadapter.qnd;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.ICacheEntrySerializer;
import com.github.ddth.cacheadapter.ICompressor;
import com.github.ddth.cacheadapter.ces.JbossCacheEntrySerializer;
import com.github.ddth.cacheadapter.ces.JdkDeflateCompressor;
import com.github.ddth.cacheadapter.ces.KryoCacheEntrySerializer;
import com.github.ddth.cacheadapter.ces.ThriftCacheEntrySerializer;
import com.github.ddth.cacheadapter.utils.ThriftUtils;
import com.github.ddth.commons.utils.SerializationUtils;

public class QndCacheEntrySize {

    private static boolean compare(Object o1, Object o2) {
        return new EqualsBuilder().append(o1, o2).isEquals();
    }

    private static void test(ICacheEntrySerializer serializer, Object value) throws Exception {
        System.out.println("========== TEST [" + serializer.getClass() + " [" + value.getClass());
        CacheEntry ce = new CacheEntry("key", value, 3600, 0);
        {
            byte[] data = serializer.serialize(ce);
            System.out.println("Size       : " + data.length);
            CacheEntry obj = serializer.deserialize(data);
            System.out.println("Deserialize: " + obj);
            System.out.println("Compare    : ===>" + compare(ce, obj));
        }
    }

    public static void main(String[] args) throws Exception {
        {
            // bootstrap
            CacheEntry ce = new CacheEntry();
            SerializationUtils.toByteArray(ce);
            SerializationUtils.toByteArrayKryo(ce);
            ThriftUtils.serialize(ce);
        }

        Value value = new Value();
        test(new JbossCacheEntrySerializer(), value);
        test(new KryoCacheEntrySerializer(), value);
        test(new ThriftCacheEntrySerializer(), value);

        System.out.println("============================================================");

        ICompressor compressor = new JdkDeflateCompressor(9);
        test(new JbossCacheEntrySerializer().setCompressor(compressor), value);
        test(new KryoCacheEntrySerializer().setCompressor(compressor), value);
        test(new ThriftCacheEntrySerializer().setCompressor(compressor), value);

        System.out.println("============================================================");

        compressor = new JdkDeflateCompressor(1);
        test(new JbossCacheEntrySerializer().setCompressor(compressor), value);
        test(new KryoCacheEntrySerializer().setCompressor(compressor), value);
        test(new ThriftCacheEntrySerializer().setCompressor(compressor), value);
    }
}
