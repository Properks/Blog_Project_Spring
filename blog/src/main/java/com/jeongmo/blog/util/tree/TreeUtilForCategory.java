package com.jeongmo.blog.util.tree;

import com.jeongmo.blog.domain.Category;

/**
 * Tree for Category
 */
public class TreeUtilForCategory {

    private TreeUtilForCategory() {}


    /**
     * Get height from category node, Root node is 0
     *
     * @param node The node, Category, which you want to find height.
     * @return The height of node, root is 0
     */
    public static int getHeight(Category node) {
        int index = 0;
        while (node.getParent() != null) { // Root node is 0
            index++;
            node = node.getParent();
        }
        return index;
    }

    /**
     * Get Category's name from path
     *
     * @param path The path of category split with "_". Ex: category1_child1_child2_child3
     * @return The name of category. Ex: child3
     */
    public static String getLeafCategory(String path) {
        if (path == null) {return null;}
        String[] paths = path.split("_");
        return paths[paths.length - 1];
    }

    /**
     * Get the parent node of last node in path
     *
     * @param path The path of child node
     * @return The parent node of last node in path
     */
    public static String getParentOfLeaf(String path) {
        if (path == null) {return null;}
        String[] paths = path.split("_");
        return (paths.length - 2 < 0) ? null : paths[paths.length - 2];
    }

    /**
     * Remove last node in the path
     *
     * @param path The path that you want to remove last node
     * @return The path removed last node
     */
    public static String getPathWithoutLeaf(String path) {
        if (path == null) {return null;}

        String[] paths = path.split("_");
        if (paths.length == 1) {return null;}

        StringBuilder builder = new StringBuilder();
        builder.append(paths[0]);
        for (int i = 1; i < paths.length - 1; i++) {
            builder.append("_");
            builder.append(paths[i]);
        }
        return builder.toString();
    }
}
