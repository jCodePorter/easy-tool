package cn.augrain.easy.tool.lang;

import java.awt.*;

/**
 * 十六进制
 *
 * @author biaoy
 * @since 2025/05/24
 */
public class HexUtils {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    /**
     * 将十六进制颜色字符串转换为 Color 对象
     *
     * @param hexColor 支持的格式: "#RRGGBB" 或 "#AARRGGBB" 或 "RRGGBB"  或 "AARRGGBB"
     * @return 对应的 Color 对象
     * @throws IllegalArgumentException 如果颜色字符串格式无效
     */
    public static Color hexToColor(String hexColor) {
        String hex = hexColor.startsWith("#") ? hexColor.substring(1) : hexColor;
        switch (hex.length()) {
            case 6:
                return new Color(
                        Integer.parseInt(hex.substring(0, 2), 16), // R
                        Integer.parseInt(hex.substring(2, 4), 16), // G
                        Integer.parseInt(hex.substring(4, 6), 16)  // B
                );
            case 8:
                return new Color(
                        Integer.parseInt(hex.substring(2, 4), 16), // R
                        Integer.parseInt(hex.substring(4, 6), 16), // G
                        Integer.parseInt(hex.substring(6, 8), 16), // B
                        Integer.parseInt(hex.substring(0, 2), 16)  // A
                );
            default:
                throw new IllegalArgumentException("Invalid hex color format: " + hexColor);
        }
    }

    public static byte[] hexToByte(String str) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty()) {
            return new byte[0];
        }
        byte[] byteArray = new byte[str.length() / 2];
        for (int i = 0; i < byteArray.length; i++) {
            String subStr = str.substring(2 * i, 2 * i + 2);
            byteArray[i] = ((byte) Integer.parseInt(subStr, 16));
        }
        return byteArray;
    }

    public static String byteToHex(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        char[] hexChars = new char[byteArray.length * 2];
        for (int j = 0; j < byteArray.length; j++) {
            int v = byteArray[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static char hexToAsc(char hex) {
        if (hex <= 9) {
            hex += 0x30;
        } else if (hex <= 15) {
            hex += 0x37;
        } else {
            hex = 0xff;
        }
        return hex;
    }

    public static char ascToHex(char aChar) {
        if ((aChar >= 0x30) && (aChar <= 0x39)) {
            aChar -= 0x30;
        } else if ((aChar >= 0x41) && (aChar <= 0x46)) {
            // 大写字母
            aChar -= 0x37;
        } else if ((aChar >= 0x61) && (aChar <= 0x66)) {
            // 小写字母
            aChar -= 0x57;
        } else {
            aChar = 0xff;
        }
        return aChar;
    }

}
