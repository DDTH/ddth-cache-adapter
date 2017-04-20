package com.github.ddth.cacheadapter.qnd;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.MemcachedClientCallable;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;

public class QndXMemcached {

    public static void main(String[] args)
            throws IOException, TimeoutException, InterruptedException, MemcachedException {
        MemcachedClientBuilder builder = new XMemcachedClientBuilder("localhost:11211");
        MemcachedClient client = builder.build();
        try {
            client.withNamespace("ns1", new MemcachedClientCallable<Void>() {
                @Override
                public Void call(MemcachedClient client)
                        throws MemcachedException, InterruptedException, TimeoutException {
                    client.set("key1", 0, "123");
                    String value1 = client.get("key1");
                    System.out.println(value1);

                    client.set("key2", 4, "value2");
                    String value2 = client.get("key2");
                    System.out.println(value2);
                    value2 = client.get("key2");
                    System.out.println(value2);

                    client.set("key3", 0, "hello".getBytes());
                    byte[] value3 = client.get("key3");
                    System.out.println(new String(value3));

                    return null;
                }
            });

            client.withNamespace("ns2", new MemcachedClientCallable<Void>() {
                @Override
                public Void call(MemcachedClient client)
                        throws MemcachedException, InterruptedException, TimeoutException {
                    client.set("key1", 0, "123");
                    String value1 = client.get("key1");
                    System.out.println(value1);

                    client.set("key2", 4, "value2");
                    String value2 = client.get("key2");
                    System.out.println(value2);
                    value2 = client.get("key2");
                    System.out.println(value2);

                    client.set("key3", 0, "hello".getBytes());
                    byte[] value3 = client.get("key3");
                    System.out.println(new String(value3));

                    return null;
                }
            });

            client.withNamespace("ns1", new MemcachedClientCallable<Void>() {
                @Override
                public Void call(MemcachedClient client)
                        throws MemcachedException, InterruptedException, TimeoutException {
                    client.set("key1", 0, "123");
                    String value1 = client.get("key1");
                    System.out.println(value1);

                    client.set("key2", 4, "value2");
                    String value2 = client.get("key2");
                    System.out.println(value2);
                    value2 = client.get("key2");
                    System.out.println(value2);

                    client.set("key3", 0, "hello".getBytes());
                    byte[] value3 = client.get("key3");
                    System.out.println(new String(value3));

                    return null;
                }
            });

            client.beginWithNamespace("ns3");
            client.set("key1", 0, "123");
            String value1 = client.get("key1");
            System.out.println(value1);

            client.set("key2", 4, "value2");
            String value2 = client.get("key2");
            System.out.println(value2);
            value2 = client.get("key2");
            System.out.println(value2);

            client.set("key3", 0, "hello".getBytes());
            byte[] value3 = client.get("key3");
            System.out.println(new String(value3));
            client.endWithNamespace();
        } finally {
            client.shutdown();
        }
    }

}
