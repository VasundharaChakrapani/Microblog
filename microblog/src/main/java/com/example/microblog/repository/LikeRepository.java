package com.example.microblog.repository;

import com.example.microblog.entity.Like;
import com.example.microblog.entity.Post;
import com.example.microblog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);
    Long countByPost(Post post);
    void deleteByPost(Post post);
}
