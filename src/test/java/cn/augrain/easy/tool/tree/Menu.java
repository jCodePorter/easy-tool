package cn.augrain.easy.tool.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author biaoy
 * @since 2025/06/07
 */
@Getter
@Setter
@AllArgsConstructor
public class Menu {

    private int id;

    private int pid;

    private String name;
}
