package com.jeongmo.blog.dto.comment;

import com.jeongmo.blog.domain.Comment;
import com.jeongmo.blog.dto.user.UserResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class CommentResponse {
    private Long id;
    private String content;
    private Long articleId;
    private UserResponse author;
    private Long parentId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.articleId = comment.getArticle().getId();
        this.author = new UserResponse(comment.getAuthor());
        this.parentId = comment.getParent() == null ? null : comment.getParent().getId();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }
}
