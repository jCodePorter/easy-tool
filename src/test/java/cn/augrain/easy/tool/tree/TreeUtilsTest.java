package cn.augrain.easy.tool.tree;

import cn.augrain.easy.tool.collection.MapUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Test
    public void testTreeObject() {
        List<Menu> menus = buildMenu();
        List<MenuVO> menuVOS = convertVO(menus);

        List<MenuVO> tree = TreeUtils.toTree(menuVOS);
        TreeUtils.print(tree);
    }


    @Test
    public void testTreeObject2() {
        List<Menu> menus = buildMenu();
        List<MenuVO> menuVOS = convertVO(menus);

        List<MenuVO> tree = TreeUtils.toTree(menuVOS, "id", "pid", "child");
        TreeUtils.print(tree);
    }

    private static List<MenuVO> convertVO(List<Menu> menus) {
        return menus.stream().map(m -> {
            MenuVO menuVO = new MenuVO();
            menuVO.setId(m.getId());
            menuVO.setPid(m.getPid());
            menuVO.setName(m.getName());
            return menuVO;
        }).collect(Collectors.toList());
    }

    private List<Menu> buildMenu() {
        List<Menu> menus = new ArrayList<>();
        menus.add(new Menu(1, 0, "订单管理"));
        menus.add(new Menu(2, 1, "共享订单"));
        menus.add(new Menu(3, 1, "充电订单"));
        menus.add(new Menu(4, 1, "洗车订单"));
        menus.add(new Menu(5, 0, "设备管理"));
        menus.add(new Menu(6, 5, "自助设备"));
        menus.add(new Menu(7, 6, "打印机"));
        menus.add(new Menu(8, 6, "售货机"));
        menus.add(new Menu(9, 5, "家用电器"));
        menus.add(new Menu(10, 9, "电视机"));
        return menus;
    }
}
