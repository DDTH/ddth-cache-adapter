package com.github.ddth.cacheadapter.qnd;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.github.ddth.cacheadapter.CacheEntry;
import com.github.ddth.cacheadapter.ICacheEntrySerializer;
import com.github.ddth.cacheadapter.ces.DefaultCacheEntrySerializer;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;

public class QndXMemcachedBinary {

    public static void main(String[] args)
            throws IOException, TimeoutException, InterruptedException, MemcachedException {
        MemcachedClientBuilder builder = new XMemcachedClientBuilder("localhost:11211");
        MemcachedClient client = builder.build();
        try {
            ICacheEntrySerializer ces = DefaultCacheEntrySerializer.instance;
            TestValue.Value v1 = new TestValue.Value();
            CacheEntry ce = new CacheEntry("key", v1);
            byte[] dataSet = ces.serialize(ce);
            client.set("key", 0, dataSet);
            System.out.println(dataSet.length);

            byte[] dataGet = client.get("key");
            System.out.println(dataGet.length);
        } finally {
            client.shutdown();
        }
    }

}
