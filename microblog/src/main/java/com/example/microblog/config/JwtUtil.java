package com.example.microblog.config;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "this-is-a-very-strong-32-char-secret!"; // keep one source of truth

    private Key getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Generate JWT token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 10)) // 10 hrs
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract username safely
    public String extractUsername(String token) {
        try {
            return extractAllClaims(token).getSubject();
        } catch (JwtException | IllegalArgumentException ex) {
            // log and return null when token is invalid/corrupt/expired
            // use your logger; below is just an example
            System.out.println("Failed to extract username from token: " + ex.getMessage());
            return null;
        }
    }

    // Validate token against a UserDetails (good practice)
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractAllClaims(token).getSubject();
            return (username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException ex) {
            System.out.println("Token validation error: " + ex.getMessage());
            return false;
        }
    }

    // Keep this if you still need a boolean-only validation (but prefer the one above)
    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token) && extractAllClaims(token) != null;
        } catch (JwtException | IllegalArgumentException ex) {
            System.out.println("Token invalid: " + ex.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            Date exp = extractAllClaims(token).getExpiration();
            return exp.before(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            // If we can't parse claims, treat as expired/invalid
            return true;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)   // throws JwtException on invalid signature, etc.
                .getBody();
    }
}
