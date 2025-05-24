package cn.augrain.easy.tool.tree;

import cn.augrain.easy.tool.bean.BeanUtils;
import cn.augrain.easy.tool.bean.ClassUtils;
import cn.augrain.easy.tool.core.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 树生成
 *
 * @author biaoy
 * @since 2025/05/24
 */
@Slf4j
public class TreeUtils {

    private TreeUtils() {

    }

    @SuppressWarnings("all")
    public static <T extends TreeNode> List<T> generateTree(List<T> nodes) {
        if (nodes.isEmpty()) {
            return nodes;
        }

        List<T> result = new ArrayList<>();
        Map<?, T> nodeMap = nodes.stream().collect(Collectors.toMap(TreeNode::id, Function.identity(), (k1, k2) -> k1));

        for (T node : nodes) {
            if (nodeMap.containsKey(node.parentId())) {
                Object parentId = node.parentId();
                if (nodeMap.get(parentId).getChildren() == null) {
                    List<T> children = new ArrayList<>();
                    nodeMap.get(parentId).setChildren(children);
                }
                nodeMap.get(parentId).getChildren().add(node);
            } else {
                result.add(node);
            }
        }
        return result;
    }

    /**
     * 将list中对象转换为目标对象，并转化为树结构
     *
     * @param source       源数据
     * @param target       目标Class对象
     * @param idName       id名称
     * @param parentName   父节点名称
     * @param childrenName 子节点名称
     * @return 转换后的节点树
     */
    public static <T> List<T> convertAndToTree(List<?> source, Class<T> target, String idName, String parentName, String childrenName) {
        List<T> list = new ArrayList<>();
        try {
            for (Object o : source) {
                T t = BeanUtils.map(o, target);
                list.add(t);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return toTree(list, idName, parentName, childrenName);
    }

    /**
     * 将list中对象转换为目标对象，并转化为树结构
     *
     * @param source       源数据
     * @param objConvert   自定义对象转换器
     * @param idName       id名称
     * @param parentName   父节点名称
     * @param childrenName 子节点名称
     * @return 转换后的节点树
     */
    public static <T> List<T> convertAndToTree(List<?> source, Function<Object, T> objConvert, String idName, String parentName, String childrenName) {
        List<T> list = new ArrayList<>();
        try {
            for (Object o : source) {
                T t = objConvert.apply(o);
                list.add(t);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return toTree(list, idName, parentName, childrenName);
    }

    /**
     * 将list为树结构
     *
     * @param collection   源数据
     * @param idName       id名称
     * @param parentName   父节点名称
     * @param childrenName 子节点名称
     * @return 转换后的节点树
     */
    public static <T> List<T> toTree(List<T> collection, String idName, String parentName, String childrenName) {
        try {
            if (collection == null || collection.isEmpty()) {
                return Collections.emptyList();
            }
            if (StringUtils.isEmpty(idName)) {
                idName = "id";
            }
            if (StringUtils.isEmpty(parentName)) {
                parentName = "parent";
            }
            if (StringUtils.isEmpty(childrenName)) {
                childrenName = "children";
            }

            // 获取目标类
            Class<?> clazz = collection.get(0).getClass();
            // id属性字段
            Field idField = ClassUtils.getField(idName, clazz);
            // 父id属性字段
            Field parentField = ClassUtils.getField(parentName, clazz);
            // 子节点属性集合字段
            Field childrenField = ClassUtils.getField(childrenName, clazz);

            // 设置为可访问
            idField.setAccessible(true);
            parentField.setAccessible(true);
            childrenField.setAccessible(true);

            Map<Object, T> idMap = new HashMap<>();
            for (T t : collection) {
                Object id = idField.get(t);
                idMap.put(id, t);
            }

            List<T> roots = new ArrayList<>();
            for (T t : collection) {
                Object pid = parentField.get(t);
                if (idMap.containsKey(pid)) {
                    Object child = childrenField.get(idMap.get(pid));
                    if (child == null) {
                        childrenField.set(idMap.get(pid), new ArrayList<T>());
                    }
                    List<T> children = (List<T>) childrenField.get(idMap.get(pid));
                    children.add(t);
                } else {
                    roots.add(t);
                }
            }
            idField.setAccessible(false);
            parentField.setAccessible(false);
            childrenField.setAccessible(false);
            return roots;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将list为树结构
     *
     * @param collection 源数据
     * @param idName     id名称
     * @param parentName 父节点名称
     * @return 转换后的节点树
     */
    public static List<Map<String, Object>> toTree(List<Map<String, Object>> collection, String idName, String parentName) {
        if (collection == null || collection.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Object, Map<String, Object>> idMap = new HashMap<>();
        for (Map<String, Object> item : collection) {
            Object id = item.get(idName);
            idMap.put(id, item);
        }

        List<Map<String, Object>> roots = new ArrayList<>();
        for (Map<String, Object> item : collection) {
            Object pid = item.get(parentName);
            if (idMap.containsKey(pid)) {
                Map<String, Object> itemMap = idMap.get(pid);
                if (!itemMap.containsKey("children")) {
                    itemMap.put("children", new ArrayList<>());
                }
                List<Map<String, Object>> child = (List<Map<String, Object>>) itemMap.get("children");
                child.add(item);
            } else {
                roots.add(item);
            }
        }
        return roots;
    }

    public static void print(List<? extends TreeNode> treeNodes) {
        if (treeNodes == null || treeNodes.isEmpty()) {
            return;
        }
        print(treeNodes, 0);
    }

    @SuppressWarnings("all")
    public static void print(List<? extends TreeNode> treeNodes, int depth) {
        for (TreeNode treeNode : treeNodes) {
            if (depth > 0) {
                String left = StringUtils.repeat(" ", depth * 4);
                log.info(left);
            }
            log.info(treeNode.toString());

            if (treeNode.getChildren() != null) {
                print(treeNode.getChildren(), depth + 1);
            }
        }
    }
}
