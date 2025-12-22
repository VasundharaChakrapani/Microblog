package com.example.microblog.dto;

import java.time.LocalDateTime;

public class PostDto {
    private Long id;
    private String content;
    private String mediaUrl;
    private LocalDateTime createdAt;
    private Long userId;
    private String username; // optional, to show the author
    private Long likes;      // total likes
    private Boolean liked;   // whether current user liked it

    public PostDto() {}

    public PostDto(Long id, String content, String mediaUrl, LocalDateTime createdAt, Long userId,
                   String username, Long likes, Boolean liked) {
        this.id = id;
        this.content = content;
        this.mediaUrl = mediaUrl;
        this.createdAt = createdAt;
        this.userId = userId;
        this.username = username;
        this.likes = likes;
        this.liked = liked;
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

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Long getLikes() { return likes; }
    public void setLikes(Long likes) { this.likes = likes; }

    public Boolean getLiked() { return liked; }
    public void setLiked(Boolean liked) { this.liked = liked; }

    public void setLikeCount(Long likes){
        this.likes=likes;
    }
    public void setLikedByCurrentUser(boolean liked){
        this.liked=liked;
    }

    
}
