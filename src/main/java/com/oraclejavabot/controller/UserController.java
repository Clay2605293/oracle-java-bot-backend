package com.oraclejavabot.controller;

import com.oraclejavabot.dto.UserRequestDTO;
import com.oraclejavabot.dto.UserResponseDTO;
import com.oraclejavabot.model.UserEntity;
import com.oraclejavabot.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO createUser(@Valid @RequestBody UserRequestDTO request) {
        return userService.createUser(request);
    }

    @GetMapping
    public List<UserEntity> getUsers() {
        return userService.getUsers();
    }
}