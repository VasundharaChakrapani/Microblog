package com.example.microblog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
//which user liked which post
@Entity
@Table(name = "posts")

public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String content;

    private String mediaUrl;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Relations
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructors
    public Post() {}

    public Post(String content, String mediaUrl, User user) {
        this.content = content;
        this.mediaUrl = mediaUrl;
        this.user = user;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
