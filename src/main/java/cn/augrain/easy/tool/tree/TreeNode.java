package cn.augrain.easy.tool.tree;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * 各种树结构节点
 *
 * @author biaoy
 * @since 2025/05/24
 */
public interface TreeNode<T> {
    /**
     * 获取节点id
     *
     * @return 树节点id
     */
    T id();

    /**
     * 获取该节点的父节点id
     *
     * @return 父节点id
     */
    T parentId();

    /**
     * 获取所有子节点
     *
     * @return 子节点列表
     */
    @JsonIgnore
    List<? extends TreeNode<T>> getChildren();

    /**
     * 设置节点的子节点列表
     *
     * @param children 子节点
     */
    void setChildren(List<? extends TreeNode<T>> children);

}
