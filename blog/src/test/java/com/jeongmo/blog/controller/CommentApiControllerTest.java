package com.jeongmo.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeongmo.blog.domain.Article;
import com.jeongmo.blog.domain.Category;
import com.jeongmo.blog.domain.Comment;
import com.jeongmo.blog.domain.User;
import com.jeongmo.blog.dto.comment.CreateCommentRequest;
import com.jeongmo.blog.dto.comment.UpdateCommentRequest;
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
                .andExpect(jsonPath("$.author.id").value(comment1.getAuthor().getId()))
                .andExpect(jsonPath("$.articleId").value(comment1.getArticle().getId()));

        replyResult.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reply1.getId()))
                .andExpect(jsonPath("$.content").value(reply1.getContent()))
                .andExpect(jsonPath("$.parentId").value(reply1.getParent().getId()))
                .andExpect(jsonPath("$.author.id").value(reply1.getAuthor().getId()))
                .andExpect(jsonPath("$.articleId").value(reply1.getArticle().getId()));
    }

    @Test
    void getComments() throws Exception{
        //given
        addComment();
        final String url = "/api/comment";

        //when
        ResultActions result = mvc.perform(get(url));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(4));

        result
                .andExpect(jsonPath("$[0].id").value(comment1.getId()))
                .andExpect(jsonPath("$[0].content").value(comment1.getContent()))
                .andExpect(jsonPath("$[0].author.id").value(comment1.getAuthor().getId()))
                .andExpect(jsonPath("$[0].articleId").value(comment1.getArticle().getId()));

        result
                .andExpect(jsonPath("$[1].id").value(comment2.getId()))
                .andExpect(jsonPath("$[1].content").value(comment2.getContent()))
                .andExpect(jsonPath("$[1].author.id").value(comment2.getAuthor().getId()))
                .andExpect(jsonPath("$[1].articleId").value(comment2.getArticle().getId()));

        result
                .andExpect(jsonPath("$[2].id").value(reply1.getId()))
                .andExpect(jsonPath("$[2].content").value(reply1.getContent()))
                .andExpect(jsonPath("$[2].author.id").value(reply1.getAuthor().getId()))
                .andExpect(jsonPath("$[2].articleId").value(reply1.getArticle().getId()));

        result
                .andExpect(jsonPath("$[3].id").value(comment3.getId()))
                .andExpect(jsonPath("$[3].content").value(comment3.getContent()))
                .andExpect(jsonPath("$[3].author.id").value(comment3.getAuthor().getId()))
                .andExpect(jsonPath("$[3].articleId").value(comment3.getArticle().getId()));
    }

    @Test
    void getCommentWithArticle() throws Exception{
        //given
        addComment();
        final String url = "/api/comment/article/{articleId}";
        final Long articleId = article1.getId();

        //when
        ResultActions result = mvc.perform(get(url, articleId));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3));

        result
                .andExpect(jsonPath("$[0].id").value(comment1.getId()))
                .andExpect(jsonPath("$[0].content").value(comment1.getContent()))
                .andExpect(jsonPath("$[0].author.id").value(comment1.getAuthor().getId()))
                .andExpect(jsonPath("$[0].articleId").value(comment1.getArticle().getId()));

        result
                .andExpect(jsonPath("$[1].id").value(reply1.getId()))
                .andExpect(jsonPath("$[1].content").value(reply1.getContent()))
                .andExpect(jsonPath("$[1].author.id").value(reply1.getAuthor().getId()))
                .andExpect(jsonPath("$[1].articleId").value(reply1.getArticle().getId()));

        result
                .andExpect(jsonPath("$[2].id").value(comment2.getId()))
                .andExpect(jsonPath("$[2].content").value(comment2.getContent()))
                .andExpect(jsonPath("$[2].author.id").value(comment2.getAuthor().getId()))
                .andExpect(jsonPath("$[2].articleId").value(comment2.getArticle().getId()));
    }

    @Test
    void getCommentWithAuthor() throws Exception{
        //given
        addComment();
        final String url = "/api/comment/author/{userId}";
        final Long userId = user.getId();

        //when
        ResultActions result = mvc.perform(get(url, userId));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));

        result
                .andExpect(jsonPath("$[0].id").value(comment1.getId()))
                .andExpect(jsonPath("$[0].content").value(comment1.getContent()))
                .andExpect(jsonPath("$[0].author.id").value(comment1.getAuthor().getId()))
                .andExpect(jsonPath("$[0].articleId").value(comment1.getArticle().getId()));

        result
                .andExpect(jsonPath("$[1].id").value(comment3.getId()))
                .andExpect(jsonPath("$[1].content").value(comment3.getContent()))
                .andExpect(jsonPath("$[1].author.id").value(comment3.getAuthor().getId()))
                .andExpect(jsonPath("$[1].articleId").value(comment3.getArticle().getId()));
    }

    @Test
    void deleteComment() throws Exception{
        //given
        addComment();
        final String url = "/api/comment/{id}";

        // When comment doesn't have reply
        //given
        final Long comment2Id = comment2.getId();

        //when
        mvc.perform(delete(url, comment2Id));

        //then
        List<Comment> case1 = commentRepository.findAll();
        assertThat(case1).hasSize(3);
        assertThat(case1.stream().map(Comment::getId).toList()).doesNotContain(comment2Id);

        // When comment have reply
        //given
        final Long comment1Id = comment1.getId();

        //when
        mvc.perform(delete(url, comment1Id));

        //then
        List<Comment> case2 = commentRepository.findAll();
        assertThat(case2).hasSize(3);
        assertThat(case2.stream().map(Comment::getId).toList()).contains(comment1Id);

        Comment foundComment = commentRepository.findById(comment1Id).get();
        assertThat(foundComment.getContent()).isEqualTo("Deleted Comment");
        assertThat(foundComment.isDeleted()).isTrue();


        // When you delete a reply while the comment is deleted
        //given
        final Long replyId = reply1.getId();

        //when
        mvc.perform(delete(url, replyId));

        //then
        List<Comment> case3 = commentRepository.findAll();
        assertThat(case3).hasSize(1);
        assertThat(case3.stream().map(Comment::getId).toList()).doesNotContain(replyId);
        assertThat(case3.stream().map(Comment::getId).toList()).doesNotContain(comment1Id);
    }

    @Test
    void updateComment() throws Exception{
        //given
        addComment();
        final String url = "/api/comment";
        final Long commentId = comment1.getId();
        final String updatedContent = "updatedComment";
        final UpdateCommentRequest dto = new UpdateCommentRequest(commentId, updatedContent);
        final String request = objectMapper.writeValueAsString(dto);

        //when
        ResultActions result = mvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(request));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(updatedContent));

        Comment foundComment = commentRepository.findById(commentId).get();
        assertThat(foundComment.getContent()).isEqualTo(updatedContent);

    }
}