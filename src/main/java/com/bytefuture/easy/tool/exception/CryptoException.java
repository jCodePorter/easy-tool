package com.bytefuture.easy.tool.exception;

/**
 * CryptoException
 *
 * @author biaoy
 * @since 2025/05/31
 */
public class CryptoException extends RuntimeException {
    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }

    public CryptoException(String message) {
        super(message);
    }
}