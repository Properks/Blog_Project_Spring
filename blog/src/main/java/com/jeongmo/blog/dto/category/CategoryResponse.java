package com.jeongmo.blog.dto.category;

import com.jeongmo.blog.domain.Category;
import com.jeongmo.blog.util.tree.TreeUtilForCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CategoryResponse {

    private Long id;
    private String name;
    private Integer height;
    private String path;

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.height = TreeUtilForCategory.getHeight(category);
        this.path = category.getPath();
    }

}
