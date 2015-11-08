package com.github.ddth.cacheadapter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.commons.io.IOUtils;
import org.jboss.serial.io.JBossObjectInputStream;
import org.jboss.serial.io.JBossObjectOutputStream;

/**
 * Default implementation of {@link ICacheEntrySerializer}.
 * 
 * <p>
 * This implementation uses JBossSerialization (http://serialization.jboss.org)
 * library to serialize/deserialize object.
 * </p>
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.3.0
 */
public class DefaultCacheEntrySerializer extends AbstractCacheEntrySerializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultCacheEntrySerializer init() {
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
    public byte[] serialize(CacheEntry ce) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            JBossObjectOutputStream oos = new JBossObjectOutputStream(baos);
            try {
                oos.writeObject(ce);
                oos.flush();
                oos.close();
                return baos.toByteArray();
            } finally {
            }
        } catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(baos);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CacheEntry deserialize(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        try {
            Class<CacheEntry> clazz = CacheEntry.class;
            JBossObjectInputStream ois = new JBossObjectInputStream(bais);
            try {
                Object obj = ois.readObject();
                if (obj != null && clazz.isAssignableFrom(obj.getClass())) {
                    return (CacheEntry) obj;
                } else {
                    return null;
                }
            } finally {
                IOUtils.closeQuietly(ois);
            }
        } catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(bais);
        }
    }

}
