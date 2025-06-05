package cn.augrain.easy.tool.collection;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SetUtils
 *
 * @author biaoy
 * @since 2025/06/05
 */
public class SetUtils {

    public static Set<String> of(String... arg) {
        return Arrays.stream(arg).collect(Collectors.toSet());
    }
}
