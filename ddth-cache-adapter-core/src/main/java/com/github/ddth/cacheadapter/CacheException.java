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

    /*----------------------------------------------------------------------*/

    /**
     * Throws to indicate that the specified cache key does not exist.
     * 
     * @author Thanh Ba Nguyen <bnguyen2k@gmail.com>
     * @since 0.6.0
     */
    public static class CacheEntryNotFoundException extends CacheException {
        private static final long serialVersionUID = "0.3.0".hashCode();

        public CacheEntryNotFoundException() {
        }

        public CacheEntryNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * Throws to indicate that the operation is not supported/allowed.
     * 
     * @author Thanh Ba Nguyen <bnguyen2k@gmail.com>
     * @since 0.6.0
     */
    public static class OperationNotSupportedException extends CacheException {
        private static final long serialVersionUID = "0.6.0".hashCode();

        public OperationNotSupportedException() {
        }

        public OperationNotSupportedException(String message) {
            super(message);
        }
    }
}
