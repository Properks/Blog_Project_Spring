package com.jeongmo.blog.dto.category;

import com.jeongmo.blog.domain.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class CategoryResponse {

    private Long id;
    private String name;
    private String path;
    private List<CategoryResponse> children;

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.path = category.getPath();
        this.children = (category.getChildren() == null) ?
                null :
                category.getChildren().stream().map(CategoryResponse::new).toList();
    }

}
