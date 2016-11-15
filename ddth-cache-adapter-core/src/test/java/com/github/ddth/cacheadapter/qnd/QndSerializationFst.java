package com.github.ddth.cacheadapter.qnd;

import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.commons.utils.SerializationUtils;

public class QndSerializationFst {

    public static void main(String[] args) {
        {
            CacheEntry ce = new CacheEntry("key", new TestValue.BaseClass());
            byte[] data = SerializationUtils.toByteArrayFst(ce);
            System.out.println(data.length + "\t" + ce);

            Object ceBack = SerializationUtils.fromByteArrayFst(data);
            System.out.println(ce.equals(ceBack) + "\t" + ceBack);

            ceBack = SerializationUtils.fromByteArrayFst(data, CacheEntry.class);
            System.out.println(ce.equals(ceBack) + "\t" + ceBack);
        }

        {
            CacheEntry ce = new CacheEntry("key", new TestValue.AClass());
            byte[] data = SerializationUtils.toByteArrayFst(ce);
            System.out.println(data.length + "\t" + ce);

            Object ceBack = SerializationUtils.fromByteArrayFst(data);
            System.out.println(ce.equals(ceBack) + "\t" + ceBack);

            ceBack = SerializationUtils.fromByteArrayFst(data, CacheEntry.class);
            System.out.println(ce.equals(ceBack) + "\t" + ceBack);
        }

        {
            TestValue.BClass obj = new TestValue.BClass();
            obj.obj = new TestValue.BaseClass();

            CacheEntry ce = new CacheEntry("key", obj);
            byte[] data = SerializationUtils.toByteArrayFst(ce);
            System.out.println(data.length + "\t" + ce);

            Object ceBack = SerializationUtils.fromByteArrayFst(data);
            System.out.println(ce.equals(ceBack) + "\t" + ceBack);

            ceBack = SerializationUtils.fromByteArrayFst(data, CacheEntry.class);
            System.out.println(ce.equals(ceBack) + "\t" + ceBack);
        }

        {
            TestValue.BClass obj = new TestValue.BClass();
            obj.obj = new TestValue.AClass();

            CacheEntry ce = new CacheEntry("key", obj);
            byte[] data = SerializationUtils.toByteArrayFst(ce);
            System.out.println(data.length + "\t" + ce);

            Object ceBack = SerializationUtils.fromByteArrayFst(data);
            System.out.println(ce.equals(ceBack) + "\t" + ceBack);

            ceBack = SerializationUtils.fromByteArrayFst(data, CacheEntry.class);
            System.out.println(ce.equals(ceBack) + "\t" + ceBack);
        }
    }
}
