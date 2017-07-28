package com.github.ddth.cacheadapter.qnd;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.test.TestValue;
import com.github.ddth.cacheadapter.utils.ThriftUtils;
import com.github.ddth.commons.utils.SerializationUtils;

public class QndCacheSize {

    private static boolean compare(Object o1, Object o2) {
        return new EqualsBuilder().append(o1, o2).isEquals();
    }

    private static void test(Object value) throws Exception {
        System.out.println("========== TEST [" + value.getClass());

        CacheEntry ce = new CacheEntry("key", value, 3600, 0);

        System.out.println("serDDTHCommons(raw): " + SerializationUtils.toByteArray(value).length);
        System.out.println(
                "serKyro(raw)       : " + SerializationUtils.toByteArrayKryo(value).length);
        System.out
                .println("serFst(raw)        : " + SerializationUtils.toByteArrayFst(value).length);
        System.out.println("serDDTHCommons(ce) : " + SerializationUtils.toByteArray(ce).length);
        System.out.println("serKyro(ce)        : " + SerializationUtils.toByteArrayKryo(ce).length);
        System.out.println("serFst(ce)         : " + SerializationUtils.toByteArrayFst(ce).length);
        System.out.println("serThrift(ce)      : " + ThriftUtils.serialize(ce).length);

        System.out.println("==== Ser/Deser (Raw):");
        {
            byte[] dataRaw = SerializationUtils.toByteArray(value);
            System.out.println("Size (Deft/raw) : " + dataRaw.length);
            Object obj = SerializationUtils.fromByteArray(dataRaw, value.getClass());
            System.out.println("Deserialize     : " + obj);
            System.out.println("Compare         : ===>" + compare(value, obj));
        }
        {
            byte[] dataRaw = SerializationUtils.toByteArrayKryo(value);
            System.out.println("Size (Kryo/raw) : " + dataRaw.length);
            Object obj = SerializationUtils.fromByteArrayKryo(dataRaw, value.getClass());
            System.out.println("Deserialize     : " + obj);
            System.out.println("Compare         : ===>" + compare(value, obj));
        }
        {
            byte[] dataRaw = SerializationUtils.toByteArrayFst(value);
            System.out.println("Size (Fst/raw)  : " + dataRaw.length);
            Object obj = SerializationUtils.fromByteArrayFst(dataRaw, value.getClass());
            System.out.println("Deserialize     : " + obj);
            System.out.println("Compare         : ===>" + compare(value, obj));
        }

        System.out.println("==== Ser/Deser (CE):");
        {
            byte[] dataCe = SerializationUtils.toByteArray(ce);
            System.out.println("Size (Deft/ce)  : " + dataCe.length);
            Object obj = SerializationUtils.fromByteArray(dataCe, CacheEntry.class);
            System.out.println("Deserialize     : " + obj);
            System.out.println("Compare         : ===>" + compare(ce, obj));
        }
        {
            byte[] dataCe = SerializationUtils.toByteArrayKryo(ce);
            System.out.println("Size (Kryo/ce)  : " + dataCe.length);
            Object obj = SerializationUtils.fromByteArrayKryo(dataCe, CacheEntry.class);
            System.out.println("Deserialize     : " + obj);
            System.out.println("Compare         : ===>" + compare(ce, obj));
        }
        {
            byte[] dataCe = SerializationUtils.toByteArrayFst(ce);
            System.out.println("Size (Fst/ce)   : " + dataCe.length);
            Object obj = SerializationUtils.fromByteArrayFst(dataCe, CacheEntry.class);
            System.out.println("Deserialize     : " + obj);
            System.out.println("Compare         : ===>" + compare(ce, obj));
        }
        {
            byte[] dataCe = ThriftUtils.serialize(ce);
            System.out.println("Size (Thrift/ce): " + dataCe.length);
            Object obj = ThriftUtils.deserialize(dataCe);
            System.out.println("Deserialize     : " + obj);
            System.out.println("Compare         : ===>" + compare(ce, obj));
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

        Map<String, Object> value1 = new HashMap<String, Object>();
        {
            value1.put("first_name", "Thành");
            value1.put("last_name", "Nguyễn");
            value1.put("email", "btnguyen2k@gmail.com");
            value1.put("dob", new Date());
        }
        test(value1);
        System.out.println("========================================");
        TestValue.Value value2 = new TestValue.Value();
        test(value2);
    }
}
