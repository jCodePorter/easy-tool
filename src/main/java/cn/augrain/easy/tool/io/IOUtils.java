package cn.augrain.easy.tool.io;

import cn.augrain.easy.tool.exception.UtilsRuntimeException;

import java.io.*;

/**
 * IOUtils
 *
 * @author biaoy
 * @since 2025/06/03
 */
public class IOUtils {

    private IOUtils() {

    }

    /**
     * 将输入流剩余内容全部读取到byte数组
     *
     * @param inputStream 输入流，读取完毕会将流关闭
     * @return 输入流剩余的内容
     */
    public static byte[] read(InputStream inputStream) {
        return read(inputStream, true);
    }

    /**
     * 将输入流剩余内容全部读取到byte数组
     *
     * @param inputStream 输入流
     * @param close       是否关闭，true表示读取完毕关闭流
     * @return 输入流剩余的内容
     */
    public static byte[] read(InputStream inputStream, boolean close) {
        byte[] buffer = new byte[2048];
        int len;

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new UtilsRuntimeException(e);
        } finally {
            if (close) {
                close(inputStream);
            }
        }
    }

    /**
     * 将数据写入到输出流中
     *
     * @param outputStream 输出流
     * @param data         要写出的数据，不能为null
     */
    public static void write(OutputStream outputStream, byte[] data) {
        write(outputStream, data, 0, data.length);
    }

    /**
     * 将数据写入到输出流中
     *
     * @param outputStream 输出流
     * @param data         要写出的数据，不能为null
     * @param offset       要写出的数据的起始位置
     * @param len          要写出数据的长度
     */
    public static void write(OutputStream outputStream, byte[] data, int offset, int len) {
        try {
            outputStream.write(data, offset, len);
            outputStream.flush();
        } catch (IOException e) {
            throw new UtilsRuntimeException(e);
        }
    }

    /**
     * 将输入流中的内容写入到输出流
     *
     * @param inputStream  输入流
     * @param outputStream 输出流
     */
    public static void write(OutputStream outputStream, InputStream inputStream) {
        write(outputStream, inputStream, false);
    }

    /**
     * 将输入流中的内容写入到输出流
     *
     * @param inputStream  输入流
     * @param outputStream 输出流
     * @param close        是否关闭输入流，true表示读取完毕关闭输入流，注意，这个关闭的是输入流，不是输出流
     */
    public static void write(OutputStream outputStream, InputStream inputStream, boolean close) {
        byte[] buffer = new byte[2048];
        int len;
        try {
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            throw new UtilsRuntimeException(e);
        } finally {
            if (close) {
                close(inputStream);
            }
        }
    }

    /**
     * 关闭指定资源
     *
     * @param closeable 要关闭的资源
     */
    public static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            throw new UtilsRuntimeException(e);
        }
    }
}
