import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Feed from './pages/Feed';
import MyPosts from './pages/MyPosts';

function App() {
  const [token, setToken] = useState(localStorage.getItem('token') || null);

  const logout = () => {
    localStorage.removeItem('token');
    setToken(null);
  };

  return (
    <Router>
      <div>
        <nav className="navbar">
          <div>
            <Link to="/"><center>Microblog</center></Link>
          </div>
          <div>
            {token ? (
              <>
                <Link to="/my-posts">My Posts</Link>
                <Link to="/">Feed</Link>
                <button onClick={logout}>Logout</button>
              </>
            ) : (
              <>
                <Link to="/login">Login</Link>
                <Link to="/register">Register</Link>
              </>
            )}
          </div>
        </nav>

        <div className="container">
          <Routes>
            <Route path="/" element={token ? <Feed token={token} /> : <Navigate to="/login" />} />
            <Route path="/login" element={<Login setToken={setToken} />} />
            <Route path="/register" element={<Register />} />
            <Route path="/my-posts" element={token ? <MyPosts token={token} /> : <Navigate to="/login" />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;
