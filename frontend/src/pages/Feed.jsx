import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import PostCard from '../components/PostCard';

function Feed({ token }) {
  const [posts, setPosts] = useState([]);
  const [content, setContent] = useState('');
  const [media, setMedia] = useState(null); // store file
  const [mediaUrl, setMediaUrl] = useState(''); // store URL for backend

useEffect(() => {
  const fetchPosts = async () => {
    try {
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      const res = await api.get('/posts');
      setPosts(res.data);
    } catch (err) {
      console.error("Error fetching posts:", err);
    }
  };
  fetchPosts();
}, [token]);

  const handleMediaChange = (e) => {
    const file = e.target.files[0];
    setMedia(file);
    if (file) {
      setMediaUrl(URL.createObjectURL(file)); // temporary URL to display/upload
    } else {
      setMediaUrl('');
    }
  };

const createPost = async (e) => {
  e.preventDefault();

  try {
    api.defaults.headers.common['Authorization'] = `Bearer ${token}`;

    const formData = new FormData();
    formData.append("content", content);
    if (media) formData.append("media", media); // real file

    const res = await api.post('/posts/new', formData, {
      headers: {
        "Content-Type": "multipart/form-data"
      }
    });

    setPosts([res.data, ...posts]);
    setContent('');
    setMedia(null);
    setMediaUrl('');
  } catch (err) {
    console.error("Error creating post:", err);
  }
};


  return (
    <div>
      <h2>Feed</h2>
      <form onSubmit={createPost}>
        <input className='inp'
          placeholder="What's on your mind?"
          value={content}
          onChange={(e) => setContent(e.target.value)}
        />
        <input type="file" accept="image/*" onChange={handleMediaChange} />
        <button type="submit">Post</button>
      </form>

      {posts.map(post => (
        <PostCard key={post.id} post={post} token={token} />
      ))}
    </div>
  );
}

export default Feed;
