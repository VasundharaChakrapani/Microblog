package com.example.microblog.controller;

import com.example.microblog.entity.User;
import com.example.microblog.service.UserService;
import com.example.microblog.service.CustomUserDetailsService;
import com.example.microblog.config.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Controller dependency
    @MockBean
    private UserService userService;

    // Security dependencies (VERY IMPORTANT)
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Test
    @WithMockUser   // ✅ Mock logged-in user
    void createUser_success() throws Exception {

        User mockUser = new User();
        mockUser.setUsername("vasu");
        mockUser.setEmail("vasu@gmail.com");

        when(userService.createUser("vasu", "vasu@gmail.com", "1234"))
                .thenReturn(mockUser);

        mockMvc.perform(post("/users/new")
                .with(csrf())   // ✅ CSRF token (fixes 403)
                .param("username", "vasu")
                .param("email", "vasu@gmail.com")
                .param("password", "1234"))
                .andExpect(status().isOk());
    }

  @Test
    @WithMockUser   // ✅ Mock logged-in user (fixes 401)
    void getAllUsers_success() throws Exception {

        User user1 = new User();
        user1.setUsername("vasu");

        User user2 = new User();
        user2.setUsername("test");

        when(userService.getAllUsers())
                .thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }
}
