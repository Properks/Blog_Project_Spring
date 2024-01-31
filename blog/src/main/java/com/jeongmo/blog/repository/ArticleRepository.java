package com.jeongmo.blog.repository;

import com.jeongmo.blog.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> getArticleByCategory_Id(Long id);

    List<Article> getArticleByAuthor_Id(Long id);

    void deleteByAuthorId(Long id);

    List<Article> getArticleByTitleContainingOrContentContaining(String titleKeywords, String contentKeywords);

    List<Article> getArticleByAuthor_NicknameContaining(String keyword);
}
