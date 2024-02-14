package com.jeongmo.blog.dto.comment;

import com.jeongmo.blog.domain.Comment;
import com.jeongmo.blog.dto.article_view.ArticleViewResponse;
import com.jeongmo.blog.dto.user.UserResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
public class CommentResponse {
    private Long id;
    private String content;
    private ArticleViewResponse article;
    private UserResponse author;
    private List<CommentResponse> replies;
    private CommentResponse parent;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.article = new ArticleViewResponse(comment.getArticle());
        this.author = new UserResponse(comment.getAuthor());
        this.replies = comment.getReplies().stream().map(CommentResponse::new).toList();
        this.parent = new CommentResponse(comment.getParent());
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }
}
