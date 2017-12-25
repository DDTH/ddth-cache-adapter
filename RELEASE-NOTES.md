# ddth-cache-adapter release notes

## 0.6.3.1 - 2017-12-24

- Fail-safe:
  - Lazy-connect: Cache should not fail because of connecting to backend system during initializing.
  - Cache operation should throw only `CacheException` when failed, underlying exception should be wrapped into `CacheEception`.
- Refactor:
  - Fewer top-level packages: `com.github.ddth.cacheadapter`, `com.github.ddth.cacheadapter.utils` and `com.github.ddth.cacheadapter.cacheimpl`.
- Bug fixes and Enhancements:
  - `ClusteredRedisCache`: `deleteAll()` support `MONOPOLISTIC` key mode.
  - Other fixes and enhancements


## 0.6.2 - 2017-07-30

- More unit test cases.
- Test case refactoring: run test cases in parallel.
- TravisCI integrated.
- Minor bug fixes  & enhancements.


## 0.6.1 - 2017-05-21

- Pass custom cache properties from factory to cache instance:
  - `AbstractCache`: new attributes `cacheProperties`
- `GuavaCache`: change default value for `cloneCacheEntries` to `true`.


## 0.6.0.2 - 2017-04-20

- Redis-based caches: general bug fixes and improvements.
- Memcached-based cache using XMemcached lib.
- New exception class `CacheException.OperationNotSupportedException`.


## 0.5.1.1 - 2017-01-04

- `CacheEntry`: Pass along the ClassLoader when serializing/deserializing.


## 0.5.1 - 2016-11-15

- Update dependencies.
- Jboss-serialization is now deprecated!
- FST as the default serialization engine (replace the deprecated Jboss serialization).


## 0.5.0.1 - 2016-10-03

- Minor enhancement: use `Class.forName(String, boolean, ClassLoader)` instead of `Class.forName(String)`.


## 0.5.0 - 2016-09-29

- Bump to `com.github.ddth:ddth-parent:6`, now requires Java 8+.
- Bump to `com.github.ddth:ddth-commons:0.5.0`.
- New class `DefaultCacheEntrySerializer`.
- Update dependencies.
- Minor fixes & enhancements.


## 0.4.1.5 - 2016-09-26

- Minor enhancements & fix a few NPEs.


## 0.4.1.4 - 2016-06-30

- `GuavaCache`: option to clone cache entries before putting to cache.


## 0.4.1 - 2016-05-17

- Switch from `ddth-redis` to [`jedis`](https://github.com/xetorthio/jedis).
- Move `*CacheEntrySerializer` classes to a separated package `com.github.ddth.cacheadapter.ces`, and
change the default cache entry serializer to `KryoCacheEntrySerializer`.
- New property `AbstractCacheEntrySerializer.compressor` and new interface `ICompressor` to support
compression of serialized cache entries.
- `ICompressor` implementation: `JdkDeflateCompressor`.
- New (experimental) `ShardedRedisCache`.
- New (experimental) `ClusteredRedisCache`.
- Bug fixes & improvements.


## 0.4.0 - 2015-11-07

- Separate artifacts: `ddth-cache-adapter-core`, `ddth-cache-adapter-redis` and `ddth-cache-adapter-serializing`.


## 0.3.1.3 - 2015-11-03

- Refactor & Enhancements.
- New class `ThriftCacheEntrySerializer`.
- Release due to a bug with Sonatype OSS' multiple client IP addresses.


## 0.3.1.1 - 2015-11-01

- Custom cache entry de/serializations.
- 2 built-in cache entry serializers: `DefaultCacheEntrySerializer` and `KryoCacheEntrySerializer`.


## 0.2.1.8 - 2015-06-30

- Bugs fixed: Redis' compact mode is not set.


## 0.2.1.7 - 2014-11-24

- Bugs fixed: cache entry's TTL.


## 0.2.0.2 - 2014-10-13

- New caches: `ThreadLocalCache` and `LocalRemoteCache`.
- Some improvements.
- Bugs fixed.


## 0.1.2 - 2014-07-30

- Fix a StackOverflowError bug in `GuavaCache` class.


## 0.1.1 - 2014-07-28

- Redis cache: Support compact and non-compact mode (default mode: non-compact).


## 0.1.0 - 2014-03-24

- First release.
