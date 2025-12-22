package com.example.microblog.controller;

import com.example.microblog.dto.LikeRequest;
import com.example.microblog.dto.PostDto;
import com.example.microblog.entity.User;
import com.example.microblog.repository.UserRepository;
import com.example.microblog.service.LikeService;
import com.example.microblog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    // ✅ Create a post (logged-in user)
   @PostMapping("/new")
public ResponseEntity<PostDto> createPost(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam("content") String content,
        @RequestParam(value = "media", required = false) MultipartFile mediaFile
) throws IOException {

    return postService.createPost(authHeader, content, mediaFile);
}


// delete post 

// Add this inside PostController
@DeleteMapping("/{postId}")
public ResponseEntity<String> deletePost(
        @RequestHeader("Authorization") String authHeader,
        @PathVariable Long postId
) {
    postService.deletePost(authHeader, postId);
    return ResponseEntity.ok("Post deleted successfully");
}



    // ✅ Get all posts (global feed)
    @GetMapping
    public List<PostDto> getAllPosts() {
        return postService.getAllPosts();
    }

    // ✅ Get posts created by the logged-in user
    @GetMapping("/me")
    public List<PostDto> getMyPosts(@RequestHeader("Authorization") String authHeader) {
        return postService.getMyPosts(authHeader);
    }

  @Autowired
    private LikeService likeService;

    @Autowired
    private UserRepository userRepository;

     @PostMapping("/{postId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {

        // get the logged-in user from security context
        String username = userDetails.getUsername();
        Long likeCount = likeService.toggleLike(postId, username);

        Map<String, Object> response = new HashMap<>();
        response.put("likes", likeCount);
        response.put("liked", likeService.isPostLikedByUser(postId, username));

        return ResponseEntity.ok(response);
    }
  

}
