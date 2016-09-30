package com.github.ddth.cacheadapter.qnd;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.ICacheEntrySerializer;
import com.github.ddth.cacheadapter.ICompressor;
import com.github.ddth.cacheadapter.ces.DefaultCacheEntrySerializer;
import com.github.ddth.cacheadapter.ces.JbossCacheEntrySerializer;
import com.github.ddth.cacheadapter.ces.KryoCacheEntrySerializer;
import com.github.ddth.cacheadapter.ces.ThriftCacheEntrySerializer;
import com.github.ddth.cacheadapter.compressor.JdkDeflateCompressor;
import com.github.ddth.cacheadapter.utils.ThriftUtils;
import com.github.ddth.commons.utils.SerializationUtils;

public class QndCacheEntrySize {

    private static boolean compare(Object o1, Object o2) {
        return new EqualsBuilder().append(o1, o2).isEquals();
    }

    private static void test(ICacheEntrySerializer serializer, Object value) throws Exception {
        System.out.println("========== TEST [" + serializer.getClass().getSimpleName() + "]: "
                + value.getClass().getSimpleName());
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

        TestValue.Value value = new TestValue.Value();
        ICompressor compressor9 = new JdkDeflateCompressor(9);
        ICompressor compressor1 = new JdkDeflateCompressor(1);

        System.out.println("===== No Compression =======================================");
        test(new DefaultCacheEntrySerializer(), value);
        test(new JbossCacheEntrySerializer(), value);
        test(new KryoCacheEntrySerializer(), value);
        test(new ThriftCacheEntrySerializer(), value);
        System.out.println();

        System.out.println("===== Compression [1] ======================================");
        test(new DefaultCacheEntrySerializer().setCompressor(compressor1), value);
        test(new JbossCacheEntrySerializer().setCompressor(compressor1), value);
        test(new KryoCacheEntrySerializer().setCompressor(compressor1), value);
        test(new ThriftCacheEntrySerializer().setCompressor(compressor1), value);
        System.out.println();

        System.out.println("===== Compression [9] ======================================");
        test(new DefaultCacheEntrySerializer().setCompressor(compressor9), value);
        test(new JbossCacheEntrySerializer().setCompressor(compressor9), value);
        test(new KryoCacheEntrySerializer().setCompressor(compressor9), value);
        test(new ThriftCacheEntrySerializer().setCompressor(compressor9), value);
        System.out.println();
    }
}
