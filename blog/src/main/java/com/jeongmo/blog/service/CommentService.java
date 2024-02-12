package com.jeongmo.blog.service;

import com.jeongmo.blog.domain.Article;
import com.jeongmo.blog.domain.Comment;
import com.jeongmo.blog.domain.User;
import com.jeongmo.blog.dto.comment.CreateCommentRequest;
import com.jeongmo.blog.dto.comment.UpdateCommentRequest;
import com.jeongmo.blog.repository.ArticleRepository;
import com.jeongmo.blog.repository.CommentRepository;
import com.jeongmo.blog.util.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;

    public Comment createComment(CreateCommentRequest request) {
        if (request.getArticleId() == null) {
            throw new IllegalArgumentException("Article id of CreateCommentRequest is null");
        }
        Article article = articleRepository.findById(request.getArticleId()).orElseThrow(() ->
                new IllegalArgumentException("Cannot find article id (createComment())"));

        Comment parent = null;
        if (request.getParent() != null) {
            parent = commentRepository.findById(request.getParent()).orElseThrow(() ->
                    new IllegalArgumentException("Cannot find comment id:" + request.getParent() + " (createComment())"));
        }
        User author = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Comment.builder()
                .content(request.getContent())
                .article(article)
                .author(author)
                .parent(parent)
                .build();
    }

    public Comment getComment(Long id) {
        return commentRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Cannot find comment(" + id + ")"));
    }

    public List<Comment> getComments() {
        return commentRepository.findAll();
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    @Transactional
    public Comment updateComment(UpdateCommentRequest request) {
        Comment updatedComment = commentRepository.findById(request.getCommentId()).orElseThrow(() ->
                new IllegalArgumentException("Cannot find comment id: "+ request.getCommentId() + " (updateComment())"));
        updatedComment.update(request.getContent());
        return updatedComment;
    }
}
