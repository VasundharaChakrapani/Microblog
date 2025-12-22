package com.example.microblog.service;

import com.example.microblog.dto.PostDto;
import com.example.microblog.entity.Like;
import com.example.microblog.entity.Post;
import com.example.microblog.entity.User;
import com.example.microblog.repository.LikeRepository;
import com.example.microblog.repository.PostRepository;
import com.example.microblog.repository.UserRepository;
import com.example.microblog.uploads.MediaStorageService;
import com.example.microblog.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    LikeService likeService;

    @Autowired
    private MediaStorageService mediaStorageService;

    @Autowired
    private JwtUtil jwtUtil;

    // Create a post for the logged-in user
   @PostMapping("/new")
public ResponseEntity<PostDto> createPost(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam("content") String content,
        @RequestParam(value = "media", required = false) MultipartFile mediaFile
) throws IOException {

    String token = authHeader.replace("Bearer ", "");
    String username = jwtUtil.extractUsername(token);

    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    String mediaUrl = null;
    if (mediaFile != null && !mediaFile.isEmpty()) {
        mediaUrl = mediaStorageService.saveMedia(mediaFile);  // store actual image
    }

    Post post = new Post();
    post.setUser(user);
    post.setContent(content);
    post.setMediaUrl(mediaUrl);  // final real URL like /media/17123123_img.png
    post.setCreatedAt(LocalDateTime.now());

    Post savedPost = postRepository.save(post);

    return ResponseEntity.ok(mapToDto(savedPost));
}



    // Get all posts (global feed)
    public List<PostDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    

    // Get posts created by the logged-in user
    public List<PostDto> getMyPosts(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return postRepository.findByUser(user).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }



//deleting posts
// delete posts

@Transactional
public void deletePost(String authHeader, Long postId) {
    String token = authHeader.replace("Bearer ", "");
    String username = jwtUtil.extractUsername(token);

    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

    // Check ownership
    if (!post.getUser().getId().equals(user.getId())) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete another user's post");
    }
    likeRepository.deleteByPost(post);

    // Delete media file if it's a local upload
    if (post.getMediaUrl() != null) {
        deleteMediaFile(post.getMediaUrl());
    }

    postRepository.delete(post);
}

// delete media file helper
private void deleteMediaFile(String mediaUrl) {
    try {
        // Skip deletion for non-local files (URLs or blob URLs)
        if (mediaUrl.startsWith("http") || mediaUrl.startsWith("blob")) {
            System.out.println("Skipping deletion for external or blob media URL: " + mediaUrl);
            return;
        }

        // Safe local path
        String fileName = mediaUrl.replace("/media/", "");
        Path filePath = Paths.get("uploads", fileName);
        Files.deleteIfExists(filePath);
        System.out.println("Deleted local media file: " + filePath.toString());
    } catch (IOException e) {
        System.out.println("Failed to delete file: " + e.getMessage());
    }
}






    
    // ====== LIKES ======
    public Long toggleLike(Long postId, User user) {
        Post post = postRepository.findById(postId) .orElseThrow(() -> new RuntimeException("Post not found with id " + postId));

        Optional<Like> existingLike = likeRepository.findByUserAndPost(user, post);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);
        }

        return likeRepository.countByPost(post); // return updated like count
    }

    // Helper: Post → PostDto
private PostDto mapToDto(Post post) {
    PostDto dto = new PostDto();
    dto.setId(post.getId());
    dto.setContent(post.getContent());
    dto.setMediaUrl(post.getMediaUrl());
    dto.setCreatedAt(post.getCreatedAt());
    dto.setUserId(post.getUser().getId());
    dto.setUsername(post.getUser().getUsername());
     dto.setLikeCount(likeService.countLikes(post));
    dto.setLikedByCurrentUser(likeService.isPostLikedByUser(post.getId(), (post.getUser().getUsername())));
    return dto;
}

}
