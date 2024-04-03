package com.jeongmo.blog.controller;

import com.jeongmo.blog.domain.Article;
import com.jeongmo.blog.domain.User;
import com.jeongmo.blog.dto.article_view.ArticleViewResponse;
import com.jeongmo.blog.dto.category.CategoryResponse;
import com.jeongmo.blog.dto.comment.CommentResponse;
import com.jeongmo.blog.service.ArticleService;
import com.jeongmo.blog.service.CategoryService;
import com.jeongmo.blog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@RequiredArgsConstructor
@Controller
public class ArticleViewController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final CommentService commentService;

    private static final String MAIN = "mainPage";
    private static final String ARTICLE_VIEW = "articleView";
    private static final String NEW_ARTICLE = "newArticle";

    /**
     * Homepage request mapping. (Article list page)
     *
     * @param page The page of home page
     * @param size The size of articles is shown on page
     * @param model The model for thymeleaf
     */
    @GetMapping("/home")
    public String mainPage(@RequestParam(required = false, defaultValue = "1") Integer page,
                           @RequestParam(required = false, defaultValue = "10") Integer size,
                           @RequestParam(required = false) Long categoryId,
                           @RequestParam(required = false) Long userId,
                           @RequestParam(required = false) String titleContent,
                           @RequestParam(required = false) String writer,
                           Model model) {
        // Set categories
        addAllCategory(model);

        // Get articles
        List<ArticleViewResponse> articles = new ArrayList<>(articleService.searchArticles(
                categoryId, titleContent, userId, writer)
                .stream()
                .map(ArticleViewResponse::new)
                .toList());

        // Set article
        Collections.reverse(articles);
        model.addAttribute("articles", articles
                .subList((page - 1) * size, Math.min(page * size, articles.size())));

        // Set page
        model.addAttribute("totalArticleSize", articles.size());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPage", (articles.size() - 1) / size + 1);
        model.addAttribute("currentPage", page);
        return MAIN;
    }

    @GetMapping("/article/{id}")
    public String viewArticle(Model model, @PathVariable Long id) {
        // Set categories
        addAllCategory(model);

        ArticleViewResponse response = new ArticleViewResponse(articleService.getArticle(id));
        model.addAttribute("viewArticle", response);
        addAllComments(model, id);
        return ARTICLE_VIEW;
    }

    @GetMapping("/new-article")
    public String newArticle(@RequestParam(required = false) Long id, Model model,
                             RedirectAttributes attributes, Authentication authentication) {
        // Set categories
        addAllCategory(model);

        if (id == null) {
            if (model.getAttribute("categories") == null) {
                attributes.addFlashAttribute("error", "404 ERROR, Category doesn't exist");
                // addFlashAttribute in RedirectAttributes: input something like model.addAttribute in redirect url.
                return "redirect:/home";
            }
            model.addAttribute(NEW_ARTICLE, new ArticleViewResponse());
        } else {
            Article foundArticle = articleService.getArticle(id);
            if (foundArticle.getAuthor().getId().equals(((User)authentication.getPrincipal()).getId())) {
                model.addAttribute(NEW_ARTICLE, new ArticleViewResponse(articleService.getArticle(id)));
            } else {
                attributes.addFlashAttribute("error", "401 ERROR, You're not writer.");
                return "redirect:/article/" + id;
            }
        }
        return NEW_ARTICLE;
    }

    /**
     * Add all category to model.
     */
    private void addAllCategory(Model model) {
        if (!categoryService.isEmpty()) {
            model.addAttribute("categories",
                    categoryService.getRootCategories()
                            .stream()
                            .map(CategoryResponse::new)
                            .toList());
        }
    }

    /**
     * Add all comment to model
     * @param model The model
     * @param articleId The id of article which has comment that you want
     */
    private void addAllComments(Model model, Long articleId) {
        List<CommentResponse> comments = commentService.getCommentsWithArticle(articleId)
                .stream()
                .map(CommentResponse::new)
                .toList();
        model.addAttribute("comments", comments);
    }
}