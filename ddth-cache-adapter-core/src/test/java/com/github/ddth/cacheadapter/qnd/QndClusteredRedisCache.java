package com.github.ddth.cacheadapter.qnd;

import java.util.Random;

import com.github.ddth.cacheadapter.ICache;
import com.github.ddth.cacheadapter.redis.ClusteredRedisCacheFactory;

public class QndClusteredRedisCache {

    public static void main(String[] args) throws Exception {
        try (ClusteredRedisCacheFactory cf = new ClusteredRedisCacheFactory()) {
            cf.setRedisHostsAndPorts("10.100.174.51:7379,10.100.174.52:7379,10.100.174.53:7379");
            cf.setRedisPassword("r3d1sp2ssw0rd");
            cf.init();

            Random RAND = new Random(System.currentTimeMillis());

            ICache cache = cf.createCache("demo");
            while (true) {
                try {
                    String keySet = "key-" + RAND.nextInt(10);
                    cache.set(keySet, keySet);
                    Thread.sleep(RAND.nextInt(1981));

                    String keyGet = "key-" + RAND.nextInt(10);
                    System.out.println("GET " + keyGet + ": " + cache.get(keyGet));
                    Thread.sleep(RAND.nextInt(1981));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
