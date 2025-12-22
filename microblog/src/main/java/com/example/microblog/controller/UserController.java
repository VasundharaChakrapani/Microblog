
package com.example.microblog.controller;

import com.example.microblog.entity.User;
import com.example.microblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // ✅ Create new user
    @PostMapping("/new")
    public User createUser(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password) {
        return userService.createUser(username, email, password);
    }

    // ✅ Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
