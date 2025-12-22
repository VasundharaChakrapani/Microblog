package com.example.microblog.dto;

public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String bio;
    private String profilePictureUrl;

    public UserDto() {}

    public UserDto(Long id, String username, String email, String bio, String profilePictureUrl) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.bio = bio;
        this.profilePictureUrl = profilePictureUrl;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
}
