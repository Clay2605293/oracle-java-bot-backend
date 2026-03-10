package com.oraclejavabot.features.users.service;


import com.oraclejavabot.features.users.model.UserEntity;
import com.oraclejavabot.features.users.repository.UserRepository;
import com.oraclejavabot.features.users.dto.UserRequestDTO;
import com.oraclejavabot.features.users.dto.UserResponseDTO;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDTO createUser(UserRequestDTO request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        if (userRepository.existsByTelegramId(request.getTelegramId())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese telegramId");
        }

        UserEntity user = new UserEntity();

        user.setPrimerNombre(request.getPrimerNombre());
        user.setApellido(request.getApellido());
        user.setTelefono(request.getTelefono());
        user.setEmail(request.getEmail());
        user.setTelegramId(request.getTelegramId());
        user.setRolId(2);
        user.setEstadoId(1);

        userRepository.save(user);

        return new UserResponseDTO(
                "Usuario creado correctamente",
                user.getEmail(),
                user.getTelegramId()
        );
    }

    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }
}