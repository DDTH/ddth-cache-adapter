package com.github.ddth.cacheadapter.qnd;

import com.github.ddth.commons.redis.JedisConnector;

import redis.clients.jedis.JedisCluster;

public class QndRedisCluster {
    public static void main(String[] args) {
        try (JedisConnector jc = new JedisConnector()) {
            jc.setRedisHostsAndPorts(
                    "localhost:7000,localhost:7001,localhost:7002,localhost:7003,localhost:7004,localhost:7005");
            jc.init();

            try (JedisCluster jcluster = jc.getJedisCluster()) {
                System.out.println(jcluster);

                System.out.println("[key]=" + jcluster.get("key"));
                System.out.println("[key]=" + jcluster.set("key", "a value"));
                System.out.println("[key]=" + jcluster.get("key"));
            }
        }
    }
}
