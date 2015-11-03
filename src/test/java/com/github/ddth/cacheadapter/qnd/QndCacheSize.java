package com.github.ddth.cacheadapter.qnd;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.TException;

import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.utils.KryoUtils;
import com.github.ddth.cacheadapter.utils.ThriftUtils;
import com.github.ddth.commons.utils.SerializationUtils;

public class QndCacheSize {

    private static byte[] serDDTHCommons(Object obj) {
        return SerializationUtils.toByteArray(obj);
    }

    private static byte[] serKyro(Object obj) {
        return KryoUtils.serialize(obj);
    }

    private static byte[] serThrift(CacheEntry ce) throws TException {
        return ThriftUtils.serialize(ce);
    }

    public static void main(String[] args) throws Exception {
        String key = "key";
        // String value = "Nguyễn Bá Thành";
        Map<String, Object> value = new HashMap<String, Object>();
        {
            value.put("first_name", "Thành");
            value.put("last_name", "Nguyễn");
            value.put("email", "btnguyen2k@gmail.com");
            value.put("dob", new Date());
        }
        CacheEntry ce = new CacheEntry(key, value, 3600, 0);

        // System.out.println("Raw                : " +
        // value.getBytes("UTF-8").length);
        System.out.println("serDDTHCommons(raw): " + serDDTHCommons(value).length);
        System.out.println("serKyro(raw)       : " + serKyro(value).length);
        System.out.println("========================================");
        System.out.println("serDDTHCommons(ce) : " + serDDTHCommons(ce).length);
        System.out.println("serKyro(ce)        : " + serKyro(ce).length);
        System.out.println("serThrift(ce)      : " + serThrift(ce).length);

        System.out.println("========================================");
        {
            byte[] dataRaw = KryoUtils.serialize(value);
            System.out.println(dataRaw.length);
            Object obj = KryoUtils.deserialize(dataRaw, HashMap.class);
            System.out.println(obj);
        }
        {
            byte[] dataCe = KryoUtils.serialize(ce);
            System.out.println(dataCe.length);
            Object obj = KryoUtils.deserialize(dataCe, CacheEntry.class);
            System.out.println(obj);
        }

        System.out.println("========================================");
        {
            byte[] dataCe = ThriftUtils.serialize(ce);
            System.out.println(dataCe.length);
            Object obj = ThriftUtils.deserialize(dataCe);
            System.out.println(obj);
        }

    }
}
