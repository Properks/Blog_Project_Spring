package com.jeongmo.blog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Getter
public class Comment implements Comparable<Comment>{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false)
    private Long id;

    /**
     * The content
     */
    @Column(name = "content", columnDefinition = "MEDIUMTEXT")
    private String content;

    /**
     * The Linked article
     */
    @ManyToOne
    @JoinColumn(name = "article_id", updatable = false)
    private Article article;

    /**
     * Person who write this
     */
    @ManyToOne
    @JoinColumn(name = "author_id", updatable = false)
    private User author;

    /**
     * The replies
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Comment> replies;

    @ManyToOne
    private Comment parent;

    @Column(name = "isDeleted")
    private boolean isDeleted;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public Comment(String content, Article article, User author, Comment parent) {
        this.content = content;
        this.article = article;
        this.author = author;
        this.parent = parent;
        this.isDeleted = false;
    }

    @Override
    public int compareTo(@NotNull Comment o) {
        return this.createdAt.compareTo(o.createdAt);
    }

    public void update(String content) {
        this.content = content;
    }

    public void setDeleted() {
        this.isDeleted = true;
        this.content = "Deleted Comment";
    }
}
