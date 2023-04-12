package com.nosbielc.blogspringdatajpaspecification.infrastructure.persistence.entities;

import com.nosbielc.blogspringdatajpaspecification.infrastructure.persistence.enums.CommentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Data
@Entity
@Table(name = "post_comment")
public class PostComment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "parent_post_comment_id")
    private PostComment parentPostComment;

    @OneToMany(mappedBy = "parentPostComment", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PostComment> childPostComments = new ArrayList<>();

    @Column(name = "review", columnDefinition = "TEXT")
    private String review;

    @Column(name = "votes")
    private Integer votes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CommentStatus status;

}
