
package com.example.microblog.service;

import com.example.microblog.entity.Like;
import com.example.microblog.entity.Post;
import com.example.microblog.entity.User;
import com.example.microblog.repository.LikeRepository;
import com.example.microblog.repository.PostRepository;
import com.example.microblog.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LikeService likeService;

    private Post post;
    private User user;

    @BeforeEach
    void setup() {
        post = new Post();
        post.setId(1L);

        user = new User();
        user.setUsername("vasu");
    }

    // TEST 1: Add like when it does not exist
    @Test
    void toggleLike_shouldAddLike_whenLikeDoesNotExist() {

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));
        when(userRepository.findByUsername("vasu"))
                .thenReturn(Optional.of(user));
        when(likeRepository.findByUserAndPost(user, post))
                .thenReturn(Optional.empty());
        when(likeRepository.countByPost(post))
                .thenReturn(1L);

        Long likeCount = likeService.toggleLike(1L, "vasu");

        verify(likeRepository, times(1))
                .save(any(Like.class));
        verify(likeRepository, never())
                .delete(any());
        assertEquals(1L, likeCount);
    }

    // TEST 2: Remove like when it already exists
    @Test
    void toggleLike_shouldRemoveLike_whenLikeExists() {

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));
        when(userRepository.findByUsername("vasu"))
                .thenReturn(Optional.of(user));
        when(likeRepository.findByUserAndPost(user, post))
                .thenReturn(Optional.of(like));
        when(likeRepository.countByPost(post))
                .thenReturn(0L);

        Long likeCount = likeService.toggleLike(1L, "vasu");

        verify(likeRepository, times(1))
                .delete(like);
        verify(likeRepository, never())
                .save(any());
        assertEquals(0L, likeCount);
    }
}
