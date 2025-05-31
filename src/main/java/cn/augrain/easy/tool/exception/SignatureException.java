package cn.augrain.easy.tool.exception;

/**
 * SignatureException
 *
 * @author biaoy
 * @since 2025/05/31
 */
public class SignatureException extends RuntimeException {
    public SignatureException(String message, Throwable cause) {
        super(message, cause);
    }

    public SignatureException(String message) {
        super(message);
    }
}