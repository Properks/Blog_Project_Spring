package com.jeongmo.blog.service;

import com.jeongmo.blog.domain.Article;
import com.jeongmo.blog.domain.Category;
import com.jeongmo.blog.domain.Comment;
import com.jeongmo.blog.domain.User;
import com.jeongmo.blog.dto.comment.CreateCommentRequest;
import com.jeongmo.blog.repository.ArticleRepository;
import com.jeongmo.blog.repository.CategoryRepository;
import com.jeongmo.blog.repository.CommentRepository;
import com.jeongmo.blog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentServiceTest {

    @Autowired
    CommentService commentService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder encoder;

    User user;
    User user2;

    Article article1;
    Article article2;

    Comment comment1;
    Comment comment2;
    Comment comment3;
    Comment reply1;

    @BeforeEach
    void init() {
        commentRepository.deleteAll();
        articleRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        addUser();
        addCategoriesAndArticles();
    }

    private void addUser() {
        //Login
        user = userRepository.save(User.builder()
                .email("test@email.com")
                .password(encoder.encode("test1234"))
                .nickname("test")
                .build());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities())
        );

        //Another person
        user2 = userRepository.save(User.builder()
                .email("test2@email.com")
                .password(encoder.encode("test1234"))
                .nickname("test2")
                .build());
    }

    private void addCategoriesAndArticles() {
        Category parent = categoryRepository.save(Category.builder().name("Parent").parent(null).build());

        article1 = articleRepository.save(Article.builder()
                .title("parentTitle1")
                .content("parentContent1")
                .category(parent).author(user)
                .build());

        article2 = articleRepository.save(Article.builder()
                .title("parentTitle2")
                .content("parentContent2")
                .category(parent)
                .author(user2)
                .build());
    }

    private void addComment() {
        comment1 = commentRepository.save(Comment.builder() //first comment in article1
                .content("comment1")
                .parent(null)
                .author(user)
                .article(article1)
                .build());
        comment2 = commentRepository.save(Comment.builder() //second comment in article1
                .content("comment2")
                .parent(null)
                .author(user2)
                .article(article1)
                .build());
        reply1 = commentRepository.save(Comment.builder() //first reply in article1
                .content("reply1")
                .parent(comment1)
                .author(user2)
                .article(article1)
                .build());
        comment3 = commentRepository.save(Comment.builder() //first comment in article2
                .content("comment3")
                .parent(null)
                .author(user)
                .article(article2)
                .build());
    }



    @Test
    void createComment() {
        //given
        final String content = "comment for test";
        CreateCommentRequest request = new CreateCommentRequest(article1.getId(), null, content);

        //when
        Comment savedComment = commentService.createComment(request);

        //then
        assertThat(savedComment.getContent()).isEqualTo(content);
        assertThat(savedComment.getArticle().getId()).isEqualTo(article1.getId());
        assertThat(savedComment.getParent()).isNull();
        assertThat(savedComment.getAuthor().getId()).isEqualTo(user.getId());
    }

    @Test
    void getComment() {
        //given
        addComment();
        final Long commentId = comment1.getId();
        final Long replyId = reply1.getId();

        //when
        Comment foundComment = commentService.getComment(commentId);
        Comment foundReply = commentService.getComment(replyId);

        //then
        assertThat(foundComment.getId()).isEqualTo(comment1.getId());
        assertThat(foundComment.getContent()).isEqualTo(comment1.getContent());
        assertThat(foundComment.getParent()).isNull();
        assertThat(foundComment.getAuthor().getId()).isEqualTo(comment1.getAuthor().getId());
        assertThat(foundComment.getArticle().getId()).isEqualTo(comment1.getArticle().getId());

        assertThat(foundReply.getId()).isEqualTo(reply1.getId());
        assertThat(foundReply.getContent()).isEqualTo(reply1.getContent());
        assertThat(foundReply.getParent().getId()).isEqualTo(reply1.getParent().getId());
        assertThat(foundReply.getAuthor().getId()).isEqualTo(reply1.getAuthor().getId());
        assertThat(foundReply.getArticle().getId()).isEqualTo(reply1.getArticle().getId());
    }

    @Test
    void getComments() {
        //given
        addComment();

        //when
        List<Comment> comments = commentService.getComments();

        //then
        assertThat(comments).hasSize(4);

        assertThat(comments.get(0).getId()).isEqualTo(comment1.getId());
        assertThat(comments.get(0).getContent()).isEqualTo(comment1.getContent());
        assertThat(comments.get(0).getAuthor().getId()).isEqualTo(comment1.getAuthor().getId());
        assertThat(comments.get(0).getArticle().getId()).isEqualTo(comment1.getArticle().getId());

        assertThat(comments.get(1).getId()).isEqualTo(comment2.getId());
        assertThat(comments.get(1).getContent()).isEqualTo(comment2.getContent());
        assertThat(comments.get(1).getAuthor().getId()).isEqualTo(comment2.getAuthor().getId());
        assertThat(comments.get(1).getArticle().getId()).isEqualTo(comment2.getArticle().getId());

        assertThat(comments.get(2).getId()).isEqualTo(reply1.getId());
        assertThat(comments.get(2).getContent()).isEqualTo(reply1.getContent());
        assertThat(comments.get(2).getAuthor().getId()).isEqualTo(reply1.getAuthor().getId());
        assertThat(comments.get(2).getArticle().getId()).isEqualTo(reply1.getArticle().getId());

        assertThat(comments.get(3).getId()).isEqualTo(comment3.getId());
        assertThat(comments.get(3).getContent()).isEqualTo(comment3.getContent());
        assertThat(comments.get(3).getAuthor().getId()).isEqualTo(comment3.getAuthor().getId());
        assertThat(comments.get(3).getArticle().getId()).isEqualTo(comment3.getArticle().getId());
    }

    @Test
    void getCommentsWithArticle() {
        //given
        addComment();
        final Long articleId = article1.getId();

        //when
        List<Comment> comments = commentService.getCommentsWithArticle(articleId);

        //then
        assertThat(comments).hasSize(3);

        assertThat(comments.get(0).getId()).isEqualTo(comment1.getId());
        assertThat(comments.get(0).getContent()).isEqualTo(comment1.getContent());
        assertThat(comments.get(0).getAuthor().getId()).isEqualTo(comment1.getAuthor().getId());
        assertThat(comments.get(0).getArticle().getId()).isEqualTo(comment1.getArticle().getId());

        assertThat(comments.get(1).getId()).isEqualTo(reply1.getId()); // chronological order
        assertThat(comments.get(1).getContent()).isEqualTo(reply1.getContent());
        assertThat(comments.get(1).getAuthor().getId()).isEqualTo(reply1.getAuthor().getId());
        assertThat(comments.get(1).getArticle().getId()).isEqualTo(reply1.getArticle().getId());

        assertThat(comments.get(2).getId()).isEqualTo(comment2.getId());
        assertThat(comments.get(2).getContent()).isEqualTo(comment2.getContent());
        assertThat(comments.get(2).getAuthor().getId()).isEqualTo(comment2.getAuthor().getId());
        assertThat(comments.get(2).getArticle().getId()).isEqualTo(comment2.getArticle().getId());

    }

    @Test
    void getCommentWithAuthor() {
    }

    @Test
    void deleteComment() {
    }

    @Test
    void updateComment() {
    }
}