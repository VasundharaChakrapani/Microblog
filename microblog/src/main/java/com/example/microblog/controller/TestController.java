package com.example.microblog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import com.example.microblog.entity.*;
import com.example.microblog.repository.PostRepository;
import com.example.microblog.repository.UserRepository;


@RestController
@RequestMapping("/test")
public class TestController {
     @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;


    @GetMapping("/protected")
    public ResponseEntity<String> protectedEndpoint() {
        return ResponseEntity.ok("You accessed a protected endpoint!");
    }

    // 1️⃣ Create a test user
    @PostMapping("/user")
    public User createUser(@RequestParam String username, @RequestParam String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("test"); // dummy password
        return userRepository.save(user);
    }

    // 2️⃣ Get all users
    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    // 3️⃣ Create a test post
    @PostMapping("/post")
    public Post createPost(@RequestParam Long userId, @RequestParam String content) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Post post = new Post();
        post.setUser(user);
        post.setContent(content);
        return postRepository.save(post);
    }

    // 4️⃣ Get all posts
    @GetMapping("/posts")
    public List<Post> getPosts() {
        return postRepository.findAll();
    }
    
}
