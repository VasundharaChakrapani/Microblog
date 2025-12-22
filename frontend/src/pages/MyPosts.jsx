import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import PostCard from '../components/PostCard';

function MyPosts({ token }) {
  const [posts, setPosts] = useState([]);

  useEffect(() => {
    const fetchMyPosts = async () => {
      try {
        const res = await api.get('/posts/me', {
          headers: { Authorization: `Bearer ${token}` }
        });
        setPosts(res.data);
      } catch (err) {
        console.error("Failed to fetch posts:", err.response?.data || err.message);
      }
    };
    fetchMyPosts();
  }, [token]);

  // ✅ Define handleDelete to remove a post from UI after deletion
  const handleDelete = (postId) => {
    setPosts(prevPosts => prevPosts.filter(post => post.id !== postId));
  };

  return (
    <div>
      <h2>My Posts</h2>

      {posts.length === 0 && <p>No posts yet.</p>}

      {posts.map(post => (   // map defines 'post'
        <PostCard
          key={post.id}
          post={post}
          token={token}
          onDelete={handleDelete}  // pass callback
        />
      ))}
    </div>
  );
}

export default MyPosts;
