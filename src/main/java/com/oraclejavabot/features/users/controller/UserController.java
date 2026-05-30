package com.oraclejavabot.features.users.controller;

import com.oraclejavabot.features.users.dto.UserOperationResponseDTO;
import com.oraclejavabot.features.users.dto.UserRequestDTO;
import com.oraclejavabot.features.users.dto.UserResponseDTO;
import com.oraclejavabot.features.users.dto.UserUpdateRequestDTO;
import com.oraclejavabot.features.users.model.UserEntity;
import com.oraclejavabot.features.users.service.UserService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @PutMapping("/{userId}")
    public UserOperationResponseDTO updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateRequestDTO request
    ) {
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    public UserOperationResponseDTO deleteUser(@PathVariable UUID userId) {
        return userService.deactivateUser(userId);
    }
}