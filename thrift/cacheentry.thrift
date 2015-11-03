/* Since v0.3.2 */
/* Serialization descriptor for cache entry */

namespace java com.github.ddth.cacheadapter.thrift

struct TCacheEntry {
    1: string key,
    2: binary value,
    3: string valueClass,
    4: i64 creationTimestampMs = 0,
    5: i64 lastAccessTimestampMs = 0,
    6: i64 expireAfterWrite = -1,
    7: i64 expireAfterAccess = -1
}
