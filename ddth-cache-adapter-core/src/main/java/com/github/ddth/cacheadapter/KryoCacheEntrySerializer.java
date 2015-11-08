package com.github.ddth.cacheadapter;

import com.github.ddth.commons.utils.SerializationUtils;

/**
 * This implementation of {@link ICacheEntrySerializer} uses Kryo library
 * (https://github.com/EsotericSoftware/kryo) to serialize/deserialize cache
 * entries.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.3.0
 */
public class KryoCacheEntrySerializer extends AbstractCacheEntrySerializer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public KryoCacheEntrySerializer init() {
		super.init();

		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
		super.destroy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] serialize(final CacheEntry ce) {
		return SerializationUtils.toByteArrayKryo(ce);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CacheEntry deserialize(final byte[] data) {
		return SerializationUtils.fromByteArrayKryo(data, CacheEntry.class);
	}
}
