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

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;

    /**
     * Create new comment by dto request.
     * @param request The request to create comment.
     * @return The created comment.
     */
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

    /**
     * Get a comment by comment id
     * @param id The id of comment which you want to find
     * @return The found comment.
     */
    public Comment getComment(Long id) {
        return commentRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Cannot find comment(" + id + ")"));
    }

    /**
     * Get All comments
     * @return The all comments
     */
    public List<Comment> getComments() {
        return commentRepository.findAll();
    }

    /**
     * Get all comments of specific article.
     * @param articleId The id of article which you want to get comments
     * @return The comments of article
     */
    @Transactional(readOnly = true)
    public List<Comment> getCommentsWithArticle(Long articleId) {
        List<Comment> roots = commentRepository.findRootComments(articleId);
        return getAllCommentWithOrder(new ArrayList<>(), roots);
    }

    /**
     *  Get all comments of specific user.
     * @param userId The id of user
     * @return The all comments of user (chronological order)
     */
    public List<Comment> getCommentWithAuthor(Long userId) {
        List<Comment> comments = commentRepository.findAllByAuthor_Id(userId).orElseThrow(() ->
                new IllegalArgumentException("Cannot find comment of user " +
                        "id: " + userId + " (getCommentWithAuthor())"));
        comments.sort(Comment::compareTo);
        return  comments;
    }

    /**
     * Delete comment.
     * @param id The id of comment which you want to delete.
     */
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    /**
     * Update comment by request
     * @param request The request which contains id and content of new comment.
     * @return The updated comment.
     */
    @Transactional
    public Comment updateComment(UpdateCommentRequest request) {
        Comment updatedComment = commentRepository.findById(request.getCommentId()).orElseThrow(() ->
                new IllegalArgumentException("Cannot find comment id: "+ request.getCommentId() + " (updateComment())"));
        updatedComment.update(request.getContent());
        return updatedComment;
    }

    /**
     * Get all comment with chronological order (recursively)
     * @param comments The container will contain all comments.
     * @param roots The root comments.
     * @return The comments which contains all replies.
     */
    private List<Comment> getAllCommentWithOrder(List<Comment> comments, List<Comment> roots) {
        if (!roots.isEmpty()) { // when roots is empty, return empty list
            roots.sort(Comment::compareTo);
            for (Comment item : roots) {
                comments.add(item);
                getAllCommentWithOrder(comments, item.getReplies());
            }
        }
        return comments;
    }
}
