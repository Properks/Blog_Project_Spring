package com.jeongmo.blog.dto.article_api;

import com.jeongmo.blog.domain.Article;
import com.jeongmo.blog.domain.Category;
import com.jeongmo.blog.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO To create Article
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateArticleRequest {
    private String title;
    private String content;

    /**
     * The id of category
     */
    private Long categoryId;

    /**
     * Build this to article
     *
     * @param author The writer of article
     * @return Return article which makes with title, content and (User) author.
     */
    public Article toEntity(User author, Category category) {
        return Article.builder()
                .title(this.title)
                .content(this.content)
                .author(author)
                .category(category)
                .build();
    }
}
