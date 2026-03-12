package com.bytefuture.easy.tool.io;

/**
 * ip工具包
 *
 * @author biaoy
 * @since 2025/11/10
 */
public class IpUtils {

    /**
     * IPv4 转无符号 long
     */
    public static long ipv4ToLong(String ipAddress) {
        String[] parts = ipAddress.split("\\.");
        if (parts.length != 4) {
            throw new IllegalArgumentException("无效的IPv4地址: " + ipAddress);
        }

        long result = 0;
        for (int i = 0; i < 4; i++) {
            int part = Integer.parseInt(parts[i]);
            if (part < 0 || part > 255) {
                throw new IllegalArgumentException("IP地址段超出范围: " + part);
            }
            result = (result << 8) | (part & 0xFFL);
        }

        return result;
    }

    /**
     * 无符号 long 转 IPv4
     */
    public static String longToIPv4(long unsignedIp) {
        if (unsignedIp < 0 || unsignedIp > 0xFFFFFFFFL) {
            throw new IllegalArgumentException("IP值超出范围: " + unsignedIp);
        }

        return ((unsignedIp >> 24) & 0xFF) + "." +
                ((unsignedIp >> 16) & 0xFF) + "." +
                ((unsignedIp >> 8) & 0xFF) + "." +
                (unsignedIp & 0xFF);
    }

    /**
     * 检查IP范围（无符号比较）
     */
    public static boolean isIpInRange(String ip, String startIp, String endIp) {
        long ipUnsigned = ipv4ToLong(ip);
        long startUnsigned = ipv4ToLong(startIp);
        long endUnsigned = ipv4ToLong(endIp);
        return ipUnsigned >= startUnsigned && ipUnsigned <= endUnsigned;
    }
}