package com.jeongmo.blog.dto.comment;

import com.jeongmo.blog.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateCommentRequest {
    /**
     * When you create comment.
     */
    private Long articleId;

    /**
     * When you create replies.
     * If you create comment, this variable is null.
     */
    private Long parent;
    private String content;
}
