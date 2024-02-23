package com.jeongmo.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                .nickname("test#12345")
                .build());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities())
        );

        //Another person
        user2 = userRepository.save(User.builder()
                .email("test2@email.com")
                .password(encoder.encode("test1234"))
                .nickname("test2#56789")
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
    void createComment() throws Exception{
        //given
        final String url = "/api/comment";
        final String content = "comment for test";
        CreateCommentRequest commentRequest = new CreateCommentRequest(article1.getId(), null, content);
        final String request = objectMapper.writeValueAsString(commentRequest);

        //when
        ResultActions result = mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(request));

        //then
        result.andExpect(status().isCreated());
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments.get(0).getContent()).isEqualTo(content);
        assertThat(comments.get(0).getArticle().getId()).isEqualTo(article1.getId());
        assertThat(comments.get(0).getParent()).isNull();
        assertThat(comments.get(0).getAuthor().getId()).isEqualTo(user.getId());

    }

    @Test
    void getComment() throws Exception{
        //given
        final String url = "/api/comment/{id}";
        addComment();
        final Long commentId = comment1.getId();
        final Long replyId = reply1.getId();

        //when
        ResultActions commentResult = mvc.perform(get(url, commentId));
        ResultActions replyResult = mvc.perform(get(url, replyId));

        //then
        commentResult.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comment1.getId()))
                .andExpect(jsonPath("$.content").value(comment1.getContent()))
                .andExpect(jsonPath("$.authorId").value(comment1.getAuthor().getId()))
                .andExpect(jsonPath("$.articleId").value(comment1.getArticle().getId()));

        replyResult.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reply1.getId()))
                .andExpect(jsonPath("$.content").value(reply1.getContent()))
                .andExpect(jsonPath("$.parentId").value(reply1.getParent().getId()))
                .andExpect(jsonPath("$.authorId").value(reply1.getAuthor().getId()))
                .andExpect(jsonPath("$.articleId").value(reply1.getArticle().getId()));
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