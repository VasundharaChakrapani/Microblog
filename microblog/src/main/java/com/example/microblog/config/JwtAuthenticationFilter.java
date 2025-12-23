package com.example.microblog.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.microblog.service.CustomUserDetailsService;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

                                        System.out.println("JWT FILTER EXECUTING → " + request.getMethod() + " " + request.getRequestURI());

        String path = request.getServletPath();

if (path != null && (path.equals("/auth/login") || path.equals("/auth/register"))) {
    filterChain.doFilter(request, response);
    return;
}


        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            log.debug("No Authorization header present");
            filterChain.doFilter(request, response);
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            log.warn("Authorization header does not start with Bearer. Header value: {}", authHeader);
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7).trim(); // trimmed just in case
        log.debug("Token received (first 20 chars): {}", (token.length() > 20 ? token.substring(0, 20) + "..." : token));

        String username = jwtUtil.extractUsername(token);
        System.out.println("JWT FILTER → username: " + username);
        if (username == null) {
            log.warn("Could not extract username from token - token invalid/expired.");
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("Authentication set for user: {}", username);
            } else {
                log.warn("Token validation failed for user: {}", username);
            }
        }

        filterChain.doFilter(request, response);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
    token = authHeader.substring(7).trim();
    System.out.println("Received token in filter: " + token); // 🔍 print here
    username = jwtUtil.extractUsername(token);
}
    }
}
