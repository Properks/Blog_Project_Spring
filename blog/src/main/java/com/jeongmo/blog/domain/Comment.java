package com.jeongmo.blog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Getter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false)
    private Long id;

    /**
     * The content
     */
    private String content;

    /**
     * The Linked article
     */
    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    /**
     * Person who write this
     */
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    /**
     * The replies
     */
    @OneToMany(mappedBy = "parent")
    private List<Comment> replies;

    @ManyToOne
    private Comment parent;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
