package com.example.microblog.repository;
import com.example.microblog.entity.Like;
import com.example.microblog.entity.Post;
import com.example.microblog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser(User user);
     // Get all posts by a specific user
    
}