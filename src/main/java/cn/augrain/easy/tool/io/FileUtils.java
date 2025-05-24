package cn.augrain.easy.tool.io;

import java.io.File;

/**
 * FileUtils
 *
 * @author biaoy
 * @since 2025/05/24
 */
public class FileUtils {

    private FileUtils() {

    }

    /**
     * 创建目录
     *
     * @param folder 路径
     * @return 创建结果
     */
    public static boolean createDirectory(String folder) {
        File dir = new File(folder);
        if (dir.exists()) {
            return true;
        } else {
            return dir.mkdirs();
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名称
     * @return 扩展名
     */
    public static String getExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }
}
