package cn.augrain.easy.tool.io;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
            if (dir.isFile()) {
                boolean delete = dir.delete();
                if (!delete) {
                    return false;
                }
                return dir.mkdirs();
            } else {
                return true;
            }
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


    /**
     * 读取文件内容为字符串
     *
     * @param file 输入文件
     * @return 文本字符串
     * @throws IOException 如果读取文件出错
     */
    public static String readString(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        }
        return content.toString();
    }

    /**
     * 读取文件内容为字节数组
     *
     * @param file 输入文件
     * @return 字节数组
     * @throws IOException 如果读取文件出错
     */
    public static byte[] readBytes(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    /**
     * 将字符串写入文本文件
     *
     * @param file 输出文件
     * @param data 文本字符串
     * @throws IOException 文件异常时
     */
    public static void writeString(File file, String data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(data);
        }
    }

    /**
     * 将字节数组写入文件
     *
     * @param file 输出文件
     * @param data 字节数组
     * @throws IOException 文件异常时
     */
    public static void writeBytes(File file, byte[] data) throws IOException {
        Files.write(file.toPath(), data);
    }

    /**
     * 复制文件
     *
     * @param source      输入文件
     * @param destination 输出文件
     * @throws IOException 文件异常时
     */
    public static void copyFile(File source, File destination) throws IOException {
        Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 删除文件或目录
     *
     * @param file 输入文件或者目录
     * @throws IOException 文件异常时
     */
    public static void delete(File file) throws IOException {
        if (file.isDirectory()) {
            // 删除目录及其内容
            try (Stream<Path> paths = Files.walk(file.toPath())) {
                paths.map(Path::toFile).forEach(File::delete);
            }
        } else {
            // 删除文件
            Files.deleteIfExists(file.toPath());
        }
    }

    /**
     * 遍历目录，获取所有文件
     *
     * @param directory 输入文件目录
     * @return 目录下所有文件
     */
    public static List<File> listFiles(File directory) {
        List<File> fileList = new ArrayList<>();
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        fileList.addAll(listFiles(file));
                    } else {
                        fileList.add(file);
                    }
                }
            }
        }
        return fileList;
    }
}
