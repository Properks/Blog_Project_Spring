package com.jeongmo.blog.dto.article_view;

import com.jeongmo.blog.domain.Article;
import com.jeongmo.blog.domain.Category;
import com.jeongmo.blog.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * The DTO for response of article view
 */
@NoArgsConstructor
@Getter
public class ArticleViewResponse {
    private Long id;
    private String title;
    private String content;
    private User author;
    private Category category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * The constructor with Article
     *
     * @param article The article which you'll use response
     */
    public ArticleViewResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.author = article.getAuthor();
        this.category = article.getCategory();
        this.createdAt = article.getCreatedAt();
        this.updatedAt = article.getUpdatedAt();
    }
}
