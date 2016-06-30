package com.github.ddth.cacheadapter.utils;

import java.lang.reflect.Method;

/**
 * Utility class.
 * 
 * @author ThanhNB
 * @since 0.4.1.3
 */
public class CacheUtils {
    /**
     * Try cloning an object.
     * 
     * @param toBeCloned
     * @return result from object's {@code clone()} method if object implements
     *         interface {@link Cloneable}, otherwise the same object is
     *         returned
     */
    public static Object tryClone(Object toBeCloned) {
        if (toBeCloned == null) {
            return null;
        }
        Object clonedObj = null;
        if (toBeCloned instanceof Cloneable) {
            try {
                Method method = Object.class.getDeclaredMethod("clone");
                method.setAccessible(true);
                clonedObj = method.invoke(toBeCloned);
            } catch (Exception e) {
                if (e instanceof CloneNotSupportedException) {
                    clonedObj = toBeCloned;
                } else {
                    throw e instanceof RuntimeException ? (RuntimeException) e
                            : new RuntimeException(e);
                }
            }
        } else {
            clonedObj = toBeCloned;
        }
        return clonedObj;
    }
}
