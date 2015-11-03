package com.github.ddth.cacheadapter.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoCallback;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;

/**
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.3.2
 */
public class KryoUtils {
    private static KryoFactory factory = new KryoFactory() {
        public Kryo create() {
            Kryo kryo = new Kryo();
            return kryo;
        }
    };
    private static KryoPool kryoPool = new KryoPool.Builder(factory).softReferences().build();

    /**
     * Serializes an object to {@code byte[]} using Kryo library.
     * 
     * @param obj
     * @return
     */
    public static byte[] serialize(final Object obj) {
        if (obj == null) {
            return null;
        }
        return kryoPool.run(new KryoCallback<byte[]>() {
            @Override
            public byte[] execute(Kryo kryo) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Output output = new Output(baos);
                kryo.writeObject(output, obj);
                output.flush();
                output.close();
                return baos.toByteArray();
            }
        });
    }

    /**
     * Deserializes an object from {@code byte[]}.
     * 
     * @param data
     * @param clazz
     * @return
     */
    public static <T> T deserialize(final byte[] data, final Class<T> clazz) {
        if (data == null) {
            return null;
        }
        return kryoPool.run(new KryoCallback<T>() {
            @Override
            public T execute(Kryo kryo) {
                Input input = new Input(new ByteArrayInputStream(data));
                return kryo.readObject(input, clazz);
            }
        });
    }
}
