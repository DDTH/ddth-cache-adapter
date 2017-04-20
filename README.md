ddth-cache-adapter
==================

DDTH's adapter for various cache backends.

Project home:
[https://github.com/DDTH/ddth-cache-adapter](https://github.com/DDTH/ddth-cache-adapter)

**`ddth-cache-adapter` requires Java 8+ since v0.5.0**


## License ##

See LICENSE.txt for details. Copyright (c) 2014-2017 Thanh Ba Nguyen.

Third party libraries are distributed under their own licenses.


## Installation #

Latest release version: `0.6.0`. See [RELEASE-NOTES.md](RELEASE-NOTES.md).

Maven dependency: if only a sub-set of `ddth-cache-adapter` functionality is used, choose the corresponding
dependency artifact(s) to reduce the number of unused jar files.

*ddth-cache-adapter-core*: in-memory caches, all other dependencies are *optional*.

```xml
<dependency>
	<groupId>com.github.ddth</groupId>
	<artifactId>ddth-cache-adapter-core</artifactId>
	<version>0.6.0</version>
</dependency>
```

*ddth-cache-adapter-memcached*: include all *ddth-cache-adapter-core* and Memcached dependencies.

```xml
<dependency>
    <groupId>com.github.ddth</groupId>
    <artifactId>ddth-cache-adapter-memcached</artifactId>
    <version>0.6.0</version>
    <type>pom</type>
</dependency>
```

*ddth-cache-adapter-redis*: include all *ddth-cache-adapter-core* and Redis dependencies.

```xml
<dependency>
    <groupId>com.github.ddth</groupId>
    <artifactId>ddth-cache-adapter-redis</artifactId>
    <version>0.6.0</version>
    <type>pom</type>
</dependency>
```


## Usage ##

1. Obtain the cache factory

```java
// in-memory cache factory, with default settings
ICacheFactory factory = new GuavaCacheFactory().init();

// cache factory with some settings
ICacheFactory factory = new GuavaCacheFactory()
    .setDefaultCacheCapacity(1000)
    .setDefaultExpireAfterAccess(3600)
    .init();

// Redis cache factory
ICacheFactory factory = new RedisCacheFactory()
    .setRedisHostAndPort("localhost:6379")
    .setRedisPassword("secret")
    .init();

// Sharded-Redis cache factory
ICacheFactory factory = new ShardedRedisCacheFactory()
    .setRedisHostsAndPorts("localhost:6379,host2:port2,host3:port3")
    .setRedisPassword("secret")
    .init();

// Clustered-Redis cache factory
ICacheFactory factory = new ClusteredRedisCacheFactory()
    .setRedisHostsAndPorts("localhost:6379,host2:port2,host3:port3")
    .init();

//Memcache cache factory
ICacheFactory factory = new XMemcachedCacheFactory()
    .setMemcachedHostsAndPorts("localhost:11211,host2:port3,host3:port3")
    .init();


// A cache factory with some specific cache settings.
Map<String, Properties> cacheProps = new HashMap<String, Properties>();
Properties propCache1 = new Properties();
propCache1.put("cache.capacity", 1000);
propCache1.put("cache.expireAfterWrite", 3600);
cacheProps.put("cacheName1", propCache1);

Properties propCache2 = new Properties();
propCache2.put("cache.capacity", 10000);
propCache2.put("cache.expireAfterAccess", 3600);
cacheProps.put("cacheName2", propCache2);

ICacheFactory factory = new GuavaCacheFactory()
    .setCacheProperties(cacheProps)
    .setDefaultCacheCapacity(100)
    .setDefaultExpireAfterAccess(900)
    .setDefaultExpireAfterWrite(1800)
    .init();
```

**Key modes for Redis caches:**

- **NAMESPACE** (default): Cache entries are grouped into namespaces. Cache keys are prefixed with cache's name. So more than one Redis-based cache instances can share one same Redis server/cluster.
- **MONOPOLISTIC**: Assuming the whole Redis server/cluster is dedicated to the Redis-based cache instance.
- **HASH**: Each cache is a Redis hash specified by cache's name, cache entries are stored within the hash. More than one Redis-based cache instances can share one Redis server/cluster.

**Key modes for Memcached caches:**

- **NAMESPACE** (default): Cache entries are grouped into namespaces. Cache keys are prefixed with cache's name. So more than one Memcached-based cache instances can share one  Memcached server.
- **MONOPOLISTIC**: Assuming the whole Memcached server is dedicated to the Memcached-based cache instance.
- **XNAMESPACE**: Cache entries are grouped into namespaces using XMemcached's namespace mechanism.


2. Obtain the cache object

```java
// get (or create) a cache with default (or pre-defined) settings
ICache cache = factory.createCache("cache1");

// get (or create) a cache with some custom settings
ICache cache = factory.createCache("cache2", capacity);
ICache cache = factory.createCache("cache2", capacity, expireAfterWrite, expireAfterAccess, cacheLoader);
```

3. Do something with the cache

```java
cache.set("key1", "value1");
Object value = cache.get("key1");
...
```

4. Destroy the factory when done

```java
((AbstractCacheFactory)factory).destroy();
```

> Note:
> 
> - `expireAfterWrite` and `expireAfterWrite` are in seconds.
