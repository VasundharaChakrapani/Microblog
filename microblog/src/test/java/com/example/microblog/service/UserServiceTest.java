
package com.example.microblog.service;

import com.example.microblog.entity.User;
import com.example.microblog.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    // -------------------- createUser --------------------

    @Test
    void createUser_success() {
        // Arrange
        User savedUser = new User();
        savedUser.setUsername("vasu");
        savedUser.setEmail("vasu@gmail.com");
        savedUser.setPassword("password");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.createUser(
                "vasu",
                "vasu@gmail.com",
                "password"
        );

        // Assert
        assertNotNull(result);
        assertEquals("vasu", result.getUsername());
        assertEquals("vasu@gmail.com", result.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    // -------------------- getAllUsers --------------------

    @Test
    void getAllUsers_success() {
        // Arrange
        User u1 = new User();
        User u2 = new User();

        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        // Act
        List<User> users = userService.getAllUsers();

        // Assert
        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
    }

    // -------------------- getByUsername --------------------

    @Test
    void getByUsername_success() {
        // Arrange
        User user = new User();
        user.setUsername("vasu");

        when(userRepository.findByUsername("vasu"))
                .thenReturn(Optional.of(user));

        // Act
        User result = userService.getByUsername("vasu");

        // Assert
        assertNotNull(result);
        assertEquals("vasu", result.getUsername());

        verify(userRepository, times(1)).findByUsername("vasu");
    }

    @Test
    void getByUsername_userNotFound() {
        // Arrange
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.getByUsername("unknown")
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("unknown");
    }
}
