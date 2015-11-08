package com.github.ddth.cacheadapter;

/**
 * Throws to indicate there has been an exception while interacting with the
 * underlying cache system.
 * 
 * @author Thanh Ba Nguyen <bnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class CacheException extends RuntimeException {

    private static final long serialVersionUID = "0.3.0".hashCode();

    public static class CacheEntryNotFoundException extends CacheException {
        private static final long serialVersionUID = "0.3.0".hashCode();
    }

    public CacheException() {
    }

    public CacheException(String message) {
        super(message);
    }

    public CacheException(Throwable cause) {
        super(cause);
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }

}
