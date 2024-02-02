package com.jeongmo.blog.dto.category;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED) // @NoArgsConstructor is needed at DTO and Entity
@AllArgsConstructor
@Getter
public class CreateCategoryRequest {
    /**
     * The ID of parent category
     */
    private Long parentId;

    /**
     * The name of new category
     */
    private String name;
}
