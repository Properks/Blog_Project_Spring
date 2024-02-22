package com.jeongmo.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeongmo.blog.domain.Article;
import com.jeongmo.blog.domain.Category;
import com.jeongmo.blog.domain.Comment;
import com.jeongmo.blog.domain.User;
import com.jeongmo.blog.repository.ArticleRepository;
import com.jeongmo.blog.repository.CategoryRepository;
import com.jeongmo.blog.repository.CommentRepository;
import com.jeongmo.blog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class CommentApiControllerTest {

    @Autowired
    WebApplicationContext context;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

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
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
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
    }

    @Test
    void getComment() {
    }

    @Test
    void getComments() {
    }

    @Test
    void getCommentWithArticle() {
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