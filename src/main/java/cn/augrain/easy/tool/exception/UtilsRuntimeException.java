package cn.augrain.easy.tool.exception;

/**
 * 工具类处理过程中抛出的异常
 *
 * @author biaoy
 * @since 2025/05/24
 */
public class UtilsRuntimeException extends RuntimeException {

    /**
     * code
     */
    private final Integer code;

    /**
     * data
     */
    private final Object data;

    /**
     * 构造函数
     *
     * @param code
     * @param message
     * @param data
     * @param cause
     */
    public UtilsRuntimeException(Integer code, String message, Object data, Exception cause) {
        super(message, cause);
        this.code = code;
        this.data = data;
    }

    /**
     * 构造函数
     *
     * @param code
     * @param message
     * @param data
     */
    public UtilsRuntimeException(Integer code, String message, Object data) {
        super(message);
        this.code = code;
        this.data = data;
    }

    /**
     * 构造函数
     *
     * @param code
     * @param message
     */
    public UtilsRuntimeException(Integer code, String message) {
        this(code, message, null);
    }

    /**
     * 构造函数
     */
    public UtilsRuntimeException(String message) {
        this(500, message, null);
    }

    public UtilsRuntimeException(Throwable cause) {
        super(cause);
        this.code = 500;
        this.data = cause.getMessage();
    }
}
