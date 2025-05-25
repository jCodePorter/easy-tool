package cn.augrain.easy.tool.numeric;

/**
 * 随机数生成
 *
 * @author biaoy
 * @since 2025/05/25
 */
public class RandomUtils {

    /**
     * 生成随机的整数
     *
     * @param min 最小值
     * @param max 最大值
     * @return 随机值
     */
    public static int randomInt(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }
}
