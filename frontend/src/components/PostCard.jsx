// import React, { useState } from 'react';
// import api from '../api/axios';
// function PostCard({ post, token, onDelete }) {
//   const [liked, setLiked] = useState(post.liked || false);
//   const [likes, setLikes] = useState(post.likes || 0);

//   const toggleLike = async () => {
//     try {
//       api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
//       const res = await api.post(`/posts/${post.id}/like`);
//       setLikes(res.data.likes);
//       setLiked(res.data.liked);
//     } catch (err) {
//       console.log(err);
//     }
//   };

// const deletePost = async () => {
//   if (!window.confirm("Delete this post permanently?")) return;

//   try {
//     const res = await api.delete(`/posts/${post.id}`, {
//       headers: { Authorization: `Bearer ${token}` } // send token explicitly
//     });
//     console.log(res.data); // optional
//     onDelete(post.id); // notify parent to remove post from UI
//   } catch (err) {
//     console.error("Delete failed:", err.response?.data || err.message);
//     alert(err.response?.data?.message || "Failed to delete post");
//   }
// };


//   const fullMediaUrl = post.mediaUrl
//     ? `http://localhost:8080${post.mediaUrl}`
//     : null;

//   const isImage = fullMediaUrl && /\.(jpg|jpeg|png|gif)$/i.test(fullMediaUrl);
//   const isVideo = fullMediaUrl && /\.(mp4|mov|webm)$/i.test(fullMediaUrl);

//   return (
//     <div style={{ borderBottom: '1px solid #ccc', padding: '10px 0' }}>
//       <h3>{post.username}</h3>
//       <p>{post.content}</p>

//       {isImage && <img src={fullMediaUrl} alt="Post Media" style={{ width: '300px', borderRadius: '10px' }} />}
//       {isVideo && <video src={fullMediaUrl} controls style={{ width: '300px', borderRadius: '10px' }} />}
      
//       <button onClick={toggleLike} style={{ display: 'block', marginTop: '10px' }}>
//         {liked ? 'Unlike' : 'Like'} ({likes})
//       </button>

//       {/* DELETE BUTTON ONLY IF onDelete is passed */}
//       {onDelete && (
//         <button
//           onClick={deletePost}
//           style={{ marginTop: '10px', backgroundColor: 'red', color: 'white', padding: '6px 12px' }}
//         >
//           Delete Post
//         </button>
//       )}
//     </div>
//   );
// }

// export default PostCard;
import React, { useState } from 'react';
import api from '../api/axios';

function PostCard({ post, token, onDelete }) {
  const [liked, setLiked] = useState(post.liked || false);
  const [likes, setLikes] = useState(post.likes || 0);

  const toggleLike = async () => {
    try {
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      const res = await api.post(`/posts/${post.id}/like`);
      setLikes(res.data.likes);
      setLiked(res.data.liked);
    } catch (err) {
      console.log(err);
    }
  };

  const deletePost = async () => {
    if (!window.confirm("Delete this post permanently?")) return;

    try {
      await api.delete(`/posts/${post.id}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      onDelete(post.id);
    } catch (err) {
      console.error("Delete failed:", err.response?.data || err.message);
      alert(err.response?.data?.message || "Failed to delete post");
    }
  };

  const fullMediaUrl = post.mediaUrl ? `http://localhost:8080${post.mediaUrl}` : null;
  const isImage = fullMediaUrl && /\.(jpg|jpeg|png|gif)$/i.test(fullMediaUrl);
  const isVideo = fullMediaUrl && /\.(mp4|mov|webm)$/i.test(fullMediaUrl);

  return (
    <div className="post-card">
      <h3>{post.username}</h3>
      <p>{post.content}</p>

      {isImage && <img src={fullMediaUrl} alt="Post Media" />}
      {isVideo && <video src={fullMediaUrl} controls />}

      <div className="post-actions">
        <button
          className="heart"
          style={{ color: liked ? 'white' : '#ff6b81' }}
          onClick={toggleLike}
        >
          {liked ? '♥' : '♡'} {likes}
        </button>

        {onDelete && (
          <button className="delete" onClick={deletePost}>
            Delete Post
          </button>
        )}
      </div>
    </div>
  );
}

export default PostCard;
