package cn.augrain.easy.tool.tree;

import cn.augrain.easy.tool.collection.MapUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TreeUtilsTest {

    @Test
    public void testTreeMap() {
        List<Map<String, Object>> list = buildTreeMap();
        List<Map<String, Object>> tree = TreeUtils.toTree(list, "id", "pid");
        TreeUtils.printMap(tree);
    }

    public List<Map<String, Object>> buildTreeMap() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(MapUtils.of("id", 1, "pid", 2));
        list.add(MapUtils.of("id", 2, "pid", 3));
        list.add(MapUtils.of("id", 3, "pid", null));
        return list;
    }
}
