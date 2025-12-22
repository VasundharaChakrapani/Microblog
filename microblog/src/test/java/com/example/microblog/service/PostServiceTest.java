
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private LikeService likeService;

    @Mock
    private MediaStorageService mediaStorageService;

    @Mock
    private JwtUtil jwtUtil;

    // ===== CREATE POST =====

    @Test
    void createPost_withoutMedia_success() throws Exception {
        String authHeader = "Bearer token123";
        String token = "token123";
        String username = "vasu";

        User user = new User();
        user.setId(1L);
        user.setUsername(username);

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Post savedPost = new Post();
        savedPost.setId(10L);
        savedPost.setUser(user);
        savedPost.setContent("Hello world");

        when(postRepository.save(any(Post.class))).thenReturn(savedPost);
        when(likeService.countLikes(any())).thenReturn(0L);
        when(likeService.isPostLikedByUser(anyLong(), anyString())).thenReturn(false);

        ResponseEntity<PostDto> response =
                postService.createPost(authHeader, "Hello world", null);

        assertEquals("Hello world", response.getBody().getContent());
        assertEquals(username, response.getBody().getUsername());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void createPost_withMedia_success() throws Exception {
        String authHeader = "Bearer token123";
        String username = "vasu";

        MultipartFile mediaFile = mock(MultipartFile.class);

        User user = new User();
        user.setId(1L);
        user.setUsername(username);

        when(jwtUtil.extractUsername("token123")).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(mediaStorageService.saveMedia(mediaFile)).thenReturn("/media/img.png");

        Post savedPost = new Post();
        savedPost.setId(1L);
        savedPost.setMediaUrl("/media/img.png");
        savedPost.setUser(user);

        when(postRepository.save(any(Post.class))).thenReturn(savedPost);
        when(likeService.countLikes(any())).thenReturn(0L);
        when(likeService.isPostLikedByUser(anyLong(), anyString())).thenReturn(false);

        ResponseEntity<PostDto> response =
                postService.createPost(authHeader, "With image", mediaFile);

        assertEquals("/media/img.png", response.getBody().getMediaUrl());
        verify(mediaStorageService).saveMedia(mediaFile);
    }

    // ===== GET ALL POSTS =====

    @Test
    void getAllPosts_success() {
        Post post = new Post();
        post.setId(1L); // set ID
        User user = new User();
        user.setId(1L);
        user.setUsername("vasu");
        post.setUser(user);

        when(postRepository.findAll()).thenReturn(List.of(post));
        when(likeService.countLikes(post)).thenReturn(5L);
        when(likeService.isPostLikedByUser(anyLong(), anyString())).thenReturn(false);

        List<PostDto> posts = postService.getAllPosts();

        assertEquals(1, posts.size());
        assertEquals("vasu", posts.get(0).getUsername());
    }

    // ===== GET MY POSTS =====

    @Test
    void getMyPosts_success() {
        String authHeader = "Bearer token123";
        String token = "token123";
        String username = "vasu";

        User user = new User();
        user.setId(1L);
        user.setUsername(username);

        Post post = new Post();
        post.setId(1L); // set ID

        post.setUser(user);

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(postRepository.findByUser(user)).thenReturn(List.of(post));
        when(likeService.countLikes(post)).thenReturn(0L);
        when(likeService.isPostLikedByUser(anyLong(), anyString())).thenReturn(false);

        List<PostDto> myPosts = postService.getMyPosts(authHeader);
        assertEquals(1, myPosts.size());
        assertEquals(username, myPosts.get(0).getUsername());
    }

    // ===== DELETE POST =====

    @Test
    void deletePost_owner_success() {
        String authHeader = "Bearer token123";
        String username = "vasu";

        User user = new User();
        user.setId(1L);
        user.setUsername(username);

        Post post = new Post();
        post.setId(10L);
        post.setUser(user);

        when(jwtUtil.extractUsername("token123")).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        postService.deletePost(authHeader, 10L);

        verify(likeRepository).deleteByPost(post);
        verify(postRepository).delete(post);
    }

    @Test
    void deletePost_notOwner_forbidden() {
        String authHeader = "Bearer token123";

        User owner = new User();
        owner.setId(1L);

        User attacker = new User();
        attacker.setId(2L);
        attacker.setUsername("hacker");

        Post post = new Post();
        post.setId(10L);
        post.setUser(owner);

        when(jwtUtil.extractUsername("token123")).thenReturn("hacker");
        when(userRepository.findByUsername("hacker")).thenReturn(Optional.of(attacker));
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        assertThrows(ResponseStatusException.class,
                () -> postService.deletePost(authHeader, 10L));
    }

    // ===== TOGGLE LIKE =====

    @Test
    void toggleLike_newLike_success() {
        User user = new User();
        Post post = new Post();
        post.setId(1L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepository.findByUserAndPost(user, post)).thenReturn(Optional.empty());
        when(likeRepository.countByPost(post)).thenReturn(1L);

        Long count = postService.toggleLike(1L, user);

        assertEquals(1L, count);
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void toggleLike_existingLike_removed() {
        User user = new User();
        Post post = new Post();
        post.setId(1L);

        Like existingLike = new Like();
        existingLike.setUser(user);
        existingLike.setPost(post);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepository.findByUserAndPost(user, post)).thenReturn(Optional.of(existingLike));
        when(likeRepository.countByPost(post)).thenReturn(0L);

        Long count = postService.toggleLike(1L, user);

        assertEquals(0L, count);
        verify(likeRepository).delete(existingLike);
    }
}
