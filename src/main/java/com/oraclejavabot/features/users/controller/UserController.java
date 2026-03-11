package com.oraclejavabot.features.users.controller;

import com.oraclejavabot.features.users.dto.UserRequestDTO;
import com.oraclejavabot.features.users.dto.UserResponseDTO;
import com.oraclejavabot.features.users.model.UserEntity;
import com.oraclejavabot.features.users.service.UserService;

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
    /**
     * Crea un nuevo usuario a partir de los datos recibidos.
     *
     * @param request DTO con los datos para crear el usuario
     * @return DTO con los datos del usuario creado
     * @throws IllegalArgumentException si ya existe un usuario con el mismo email o
     *                                  telegramId
     */
    public UserResponseDTO createUser(@Valid @RequestBody UserRequestDTO request) {
        return userService.createUser(request);
    }

    @GetMapping
    /**
     * Recupera la lista de usuarios registrados.
     *
     * @return lista de {@link UserEntity} con los usuarios almacenados
     */
    public List<UserEntity> getUsers() {
        return userService.getUsers();
    }
}