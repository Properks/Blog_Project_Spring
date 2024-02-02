package com.jeongmo.blog.dto.category;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class UpdateCategoryRequest {
    /**
     * The ID of category which you want to update
     */
    private Long categoryId;

    /**
     * The ID of new parent category
     */
    private Long newParent;

    /**
     * The new name of category
     */
    private String newName;
}
