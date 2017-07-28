package com.github.ddth.cacheadapter.qnd;

import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.test.TestValue;
import com.github.ddth.commons.utils.SerializationUtils;

public class QndArraySerializationKryo {

    public static void main(String[] args) {
        {
            final int numEntries = 3;
            TestValue.BaseClass[] value = new TestValue.BaseClass[numEntries];
            for (int i = 0; i < numEntries; i++) {
                value[i] = new TestValue.BaseClass();
            }
            CacheEntry ce = new CacheEntry("key", value);
            byte[] data = SerializationUtils.toByteArrayKryo(ce);
            System.out.println(data.length + "\t" + ce);

            Object ceBack = SerializationUtils.fromByteArrayKryo(data);
            System.out.println(ce.equals(ceBack) + "\t" + ceBack);

            ceBack = SerializationUtils.fromByteArrayKryo(data, CacheEntry.class);
            System.out.println(ce.equals(ceBack) + "\t" + ceBack);
        }

        {
            final int numEntries = 3;
            TestValue.AClass[] value = new TestValue.AClass[numEntries];
            for (int i = 0; i < numEntries; i++) {
                value[i] = new TestValue.AClass();
            }
            CacheEntry ce = new CacheEntry("key", value);
            byte[] data = SerializationUtils.toByteArrayKryo(ce);
            System.out.println(data.length + "\t" + ce);

            Object ceBack = SerializationUtils.fromByteArrayKryo(data);
            System.out.println(ce.equals(ceBack) + "\t" + ceBack);

            ceBack = SerializationUtils.fromByteArrayKryo(data, CacheEntry.class);
            System.out.println(ce.equals(ceBack) + "\t" + ceBack);
        }

        {
            final int numEntries = 3;
            TestValue.BClass[] value = new TestValue.BClass[numEntries];
            for (int i = 0; i < numEntries; i++) {
                value[i] = new TestValue.BClass();
                value[i].obj = new TestValue.BaseClass();
            }
            CacheEntry ce = new CacheEntry("key", value);
            byte[] data = SerializationUtils.toByteArrayKryo(ce);
            System.out.println(data.length + "\t" + ce);

            Object ceBack = SerializationUtils.fromByteArrayKryo(data);
            System.out.println(ce.equals(ceBack) + "\t" + ceBack);

            ceBack = SerializationUtils.fromByteArrayKryo(data, CacheEntry.class);
            System.out.println(ce.equals(ceBack) + "\t" + ceBack);
        }

        {
            final int numEntries = 3;
            TestValue.BClass[] value = new TestValue.BClass[numEntries];
            for (int i = 0; i < numEntries; i++) {
                value[i] = new TestValue.BClass();
                value[i].obj = new TestValue.AClass();
            }
            CacheEntry ce = new CacheEntry("key", value);
            byte[] data = SerializationUtils.toByteArrayKryo(ce);
            System.out.println(data.length + "\t" + ce);

            Object ceBack = SerializationUtils.fromByteArrayKryo(data);
            System.out.println(ce.equals(ceBack) + "\t" + ceBack);

            ceBack = SerializationUtils.fromByteArrayKryo(data, CacheEntry.class);
            System.out.println(ce.equals(ceBack) + "\t" + ceBack);
        }

        {
            final int numEntries = 3;
            TestValue.CClass[] value = new TestValue.CClass[numEntries];
            for (int i = 0; i < numEntries; i++) {
                value[i] = new TestValue.CClass();
            }
            CacheEntry ce = new CacheEntry("key", value);
            byte[] data = SerializationUtils.toByteArrayKryo(ce);
            System.out.println(data.length + "\t" + ce);

            Object ceBack = SerializationUtils.fromByteArrayKryo(data);
            System.out.println(ce.equals(ceBack) + "\t" + ceBack);

            ceBack = SerializationUtils.fromByteArrayKryo(data, CacheEntry.class);
            System.out.println(ce.equals(ceBack) + "\t" + ceBack);
        }
    }
}
