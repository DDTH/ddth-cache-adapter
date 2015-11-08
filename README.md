ddth-cache-adapter
==================

DDTH's adapter for various cache systems.

Project home:
[https://github.com/DDTH/ddth-cache-adapter](https://github.com/DDTH/ddth-cache-adapter)

OSGi environment: `ddth-cache-adapter` is packaged as an OSGi bundle.


## License ##

See LICENSE.txt for details. Copyright (c) 2014-2015 Thanh Ba Nguyen.

Third party libraries are distributed under their own licenses.


## Installation #

Latest release version: `0.4.0`. See [RELEASE-NOTES.md](RELEASE-NOTES.md).

Maven dependency: if only a sub-set of `ddth-cache-adapter` functionality is used, choose the corresponding
dependency artifact(s) to reduce the number of unused jar files.

*ddth-cache-adapter-core*: in-memory cache, all other dependencies *optional*.

```xml
<dependency>
	<groupId>com.github.ddth</groupId>
	<artifactId>ddth-cache-adapter-core</artifactId>
	<version>0.4.0</version>
</dependency>
```

*ddth-cache-adapter-redis*: include all *ddth-cache-adapter-core* and Redis dependencies.

```xml
<dependency>
    <groupId>com.github.ddth</groupId>
    <artifactId>ddth-cache-adapter-redis</artifactId>
    <version>0.4.0</version>
    <type>pom</type>
</dependency>
```


## Usage ##

1. Obtain the cache factory

```java
// in-memory cache factory, with default settings
ICacheFactory factory = new GuavaCacheFactory().init();

// fache factory with some settings
ICacheFactory factory = new GuavaCacheFactory()
    .setDefaultCacheCapacity(1000)
    .setDefaultExpireAfterAccess(3600)
    .init();

// Redis cache factory
PoolConfig poolConfig = new PoolConfig()
    .setMaxIdle(16).setMinIdle(4)
    .setMaxActive(64).setMaxWaitTime(1000);
ICacheFactory factory = new RedisCacheFactory()
    .setRedisHost("localhost").setRedisPort(6379)
    .setPoolConfig(poolConfig)
    .init();

// A cache factory with some specific cache settings.
Map<String, Properties> cacheProps = new HashMap<String, Properties>();
Properties propCache1 = new Properties();
propCache1.put("cache.capacity", 1000);
propCache1.put("cache.expireAfterWrite", 3600);
cacheProps.put("cache1", propCache1);
Properties propCache2 = new Properties();
propCache1.put("cache.capacity", 10000);
propCache1.put("cache.expireAfterAccess", 3600);
cacheProps.put("cache2", propCache1);
ICacheFactory factory = new GuavaCacheFactory()
    .setCacheProperties(cacheProps)
    .setDefaultCacheCapacity(100)
    .setDefaultExpireAfterAccess(900)
    .setDefaultExpireAfterWrite(1800)
    .init();
```

> Compact mode for Redis cache:
> 
> With compact-mode=on, each cache is a Redis hash; cache entries are stored with in the hash.
> 
> Witn compact-mode=off, each cache entry is prefixed by cache-name and store to Redis' top-level key:value storage.
>
>
> Compact-mode can be set via `RedisCacheFactory.setCompactMode(boolean)`
>
> Default behavior: compact-mode=off.


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
> - Argument value for `PoolConfig.setMaxWaitTime(...)` is in milliseconds.
