package com.github.ddth.cacheadapter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoCallback;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;

/**
 * This implementation of {@link ICacheEntrySerializer} use Kryo library
 * (https://github.com/EsotericSoftware/kryo) to serialize/deserialize cache
 * entries..
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.3.0
 */
public class KryoCacheEntrySerializer implements ICacheEntrySerializer {

    private KryoPool kryoPool;

    public KryoCacheEntrySerializer init() {
        KryoFactory factory = new KryoFactory() {
            public Kryo create() {
                Kryo kryo = new Kryo();
                return kryo;
            }
        };
        kryoPool = new KryoPool.Builder(factory).softReferences().build();

        return this;
    }

    public void destroy() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] serialize(final CacheEntry ce) {
        if (ce == null) {
            return null;
        }
        return kryoPool.run(new KryoCallback<byte[]>() {
            @Override
            public byte[] execute(Kryo kryo) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Output output = new Output(baos);
                kryo.writeObject(output, ce);
                output.flush();
                output.close();
                return baos.toByteArray();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CacheEntry deserialize(final byte[] data) {
        if (data == null) {
            return null;
        }
        return kryoPool.run(new KryoCallback<CacheEntry>() {
            @Override
            public CacheEntry execute(Kryo kryo) {
                Input input = new Input(new ByteArrayInputStream(data));
                return kryo.readObject(input, CacheEntry.class);
            }
        });
    }

}
