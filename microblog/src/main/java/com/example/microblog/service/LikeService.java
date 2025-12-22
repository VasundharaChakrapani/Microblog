package com.example.microblog.service;

import com.example.microblog.entity.Like;
import com.example.microblog.entity.Post;
import com.example.microblog.entity.User;
import com.example.microblog.repository.LikeRepository;
import com.example.microblog.repository.PostRepository;
import com.example.microblog.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service

public class LikeService {
      private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    
    public LikeService(LikeRepository likeRepository,
                       PostRepository postRepository,
                       UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Long toggleLike(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Like> existingLike = likeRepository.findByUserAndPost(user, post);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);
        }

        return likeRepository.countByPost(post);
    }

    //checks if the  current user has liked the  post..-- for UI purpose
    public boolean isPostLikedByUser(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return likeRepository.findByUserAndPost(user, post).isPresent();
    }


    public Long countLikes(Post post) {
    return likeRepository.countByPost(post);
}




}
