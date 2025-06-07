package cn.augrain.easy.tool.tree;

import cn.augrain.easy.tool.bean.JSONUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author biaoy
 * @since 2025/06/07
 */
@Getter
@Setter
public class MenuVO implements TreeNode<Integer> {

    private Integer id;

    private Integer pid;

    private String name;

    @JsonIgnore
    private List<MenuVO> child;

    @Override
    public Integer id() {
        return id;
    }

    @Override
    public Integer parentId() {
        return pid;
    }

    @Override
    public List<? extends TreeNode<Integer>> getChildren() {
        return child;
    }

    @Override
    public void setChildren(List<? extends TreeNode<Integer>> children) {
        this.child = (List<MenuVO>) children;
    }

    @Override
    public String toString() {
        return JSONUtils.toString(this);
    }
}
