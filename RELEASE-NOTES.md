ddth-cache-adapter release notes
================================

0.5.0 - 2016-09-29
------------------

- Bump to `com.github.ddth:ddth-parent:6`, now requires Java 8+.
- Bump to `com.github.ddth:ddth-commons:0.5.0`.
- Update dependencies.
- Minor fixes & enhancements.


0.4.1.5 - 2016-09-26
--------------------

- Minor enhancements & fix a few NPEs.


0.4.1.4 - 2016-06-30
--------------------

- `GuavaCache`: option to clone cache entries before putting to cache.


0.4.1 - 2016-05-17
------------------

- Switch from `ddth-redis` to [`jedis`](https://github.com/xetorthio/jedis).
- Move `*CacheEntrySerializer` classes to a separated package `com.github.ddth.cacheadapter.ces`, and
change the default cache entry serializer to `KryoCacheEntrySerializer`.
- New property `AbstractCacheEntrySerializer.compressor` and new interface `ICompressor` to support
compression of serialized cache entries.
- `ICompressor` implementation: `JdkDeflateCompressor`.
- New (experimental) `ShardedRedisCache`.
- New (experimental) `ClusteredRedisCache`.
- Bug fixes & improvements.


0.4.0 - 2015-11-07
------------------

- Separate artifacts: `ddth-cache-adapter-core`, `ddth-cache-adapter-redis` and `ddth-cache-adapter-serializing`.


0.3.1.3 - 2015-11-03
--------------------

- Refactor & Enhancements.
- New class `ThriftCacheEntrySerializer`.
- Release due to a bug with Sonatype OSS' multiple client IP addresses.


0.3.1.1 - 2015-11-01
--------------------

- Custom cache entry de/serializations.
- 2 built-in cache entry serializers: `DefaultCacheEntrySerializer` and `KryoCacheEntrySerializer`.


0.2.1.8 - 2015-06-30
--------------------

- Bugs fixed: Redis' compact mode is not set.


0.2.1.7 - 2014-11-24
--------------------

- Bugs fixed: cache entry's TTL.


0.2.0.2 - 2014-10-13
--------------------

- New caches: `ThreadLocalCache` and `LocalRemoteCache`.
- Some improvements.
- Bugs fixed.


0.1.2 - 2014-07-30
------------------

- Fix a StackOverflowError bug in `GuavaCache` class.


0.1.1 - 2014-07-28
------------------

- Redis cache: Support compact and non-compact mode (default mode: non-compact).


0.1.0 - 2014-03-24
------------------

- First release.
