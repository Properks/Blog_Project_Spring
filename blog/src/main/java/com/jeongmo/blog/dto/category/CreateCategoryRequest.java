package com.jeongmo.blog.dto.category;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED) // @NoArgsConstructor is needed at DTO and Entity
@AllArgsConstructor
@Getter
public class CreateCategoryRequest {
    private Long parentId;
    private String name;
}
