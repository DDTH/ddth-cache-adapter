package com.github.ddth.cacheadapter.qnd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.ddth.cacheadapter.CacheEntry;

public class QndCacheSize {

    private static byte[] ser1(Serializable obj) {
        return SerializationUtils.serialize(obj);
    }

    private static byte[] ser2(Object obj) {
        return com.github.ddth.commons.utils.SerializationUtils.toByteArray(obj);
    }

    private static byte[] ser3(Object obj) {
        Kryo kryo = new Kryo();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos);
        kryo.writeObject(output, obj);
        output.flush();
        output.close();
        return baos.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        String key = "key";
        String value = "Nguyễn Bá Thành";
        CacheEntry ce = new CacheEntry(key, value, 3600, 0);

        System.out.println("Raw    : " + value.getBytes("UTF-8").length);
        System.out.println("SerRaw1: " + ser1(value).length);
        System.out.println("SerRaw2: " + ser2(value).length);
        System.out.println("SerRaw3: " + ser3(value).length);
        System.out.println("SerCE1: " + ser1(ce).length);
        System.out.println("SerCE2: " + ser2(ce).length);
        System.out.println("SerCE3: " + ser3(ce).length);

        {
            byte[] dataRaw = ser3(value);
            System.out.println(dataRaw.length);
            Kryo kryo = new Kryo();
            Input input = new Input(new ByteArrayInputStream(dataRaw));
            Object obj = kryo.readObject(input, String.class);
            System.out.println(obj);
        }
        {
            byte[] dataCe = ser3(ce);
            Kryo kryo = new Kryo();
            Input input = new Input(new ByteArrayInputStream(dataCe));
            Object obj = kryo.readObject(input, CacheEntry.class);
            System.out.println(obj);
        }

    }
}
