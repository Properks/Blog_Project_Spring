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
    }

    @Test
    void getComments() {
    }

    @Test
    void getCommentsWithArticle() {
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