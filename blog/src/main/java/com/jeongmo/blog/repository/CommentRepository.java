package com.jeongmo.blog.repository;

import com.jeongmo.blog.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.parent IS NULL AND c.article.id = :articleId")
    List<Comment> findRootComments(Long articleId);

    Optional<List<Comment>> findAllByAuthor_Id(Long id);
}
