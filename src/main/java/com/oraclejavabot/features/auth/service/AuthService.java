package com.oraclejavabot.features.auth.service;

import com.oraclejavabot.features.auth.dto.LoginRequestDTO;
import com.oraclejavabot.features.auth.dto.LoginResponseDTO;
import com.oraclejavabot.features.auth.dto.RegisterRequestDTO;
import com.oraclejavabot.features.auth.model.UserCredentialEntity;
import com.oraclejavabot.features.auth.repository.UserCredentialRepository;
import com.oraclejavabot.features.users.model.UserEntity;
import com.oraclejavabot.features.users.repository.UserRepository;
import com.oraclejavabot.features.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserCredentialRepository credentialRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository,
                       UserCredentialRepository credentialRepository,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
        this.jwtService = jwtService;
    }

    public void register(RegisterRequestDTO request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email ya registrado");
        }

        if (userRepository.existsByTelegramId(request.getTelegramId())) {
            throw new RuntimeException("Telegram ID ya registrado");
        }

        if (request.getRolId() == 2 &&
                (request.getManagerEmail() == null || request.getManagerEmail().isBlank())) {
            throw new RuntimeException("Un developer debe tener manager");
        }

        UserEntity manager = null;

        if (request.getManagerEmail() != null && !request.getManagerEmail().isBlank()) {
            manager = userRepository.findByEmail(request.getManagerEmail())
                    .orElseThrow(() -> new RuntimeException("Manager no encontrado"));
        }

        UserEntity user = new UserEntity();
        user.setPrimerNombre(request.getPrimerNombre());
        user.setApellido(request.getApellido());
        user.setEmail(request.getEmail());
        user.setTelefono(request.getTelefono());
        user.setTelegramId(request.getTelegramId());
        user.setRolId(request.getRolId());
        user.setEstadoId(1);

        if (manager != null) {
            user.setManagerId(manager.getUserId());
        }

        user = userRepository.save(user);

        String hash = passwordEncoder.encode(request.getPassword());

        UserCredentialEntity credential = new UserCredentialEntity();
        credential.setCredencialId(UUID.randomUUID());
        credential.setUser(user);
        credential.setPasswordHash(hash);
        credential.setActivo(1);

        credentialRepository.save(credential);
    }

    public LoginResponseDTO login(LoginRequestDTO request) {

        System.out.println("Intentando login con email: " + request.getEmail());

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UserCredentialEntity credential = credentialRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException(
                        "Credencial no encontrada para usuario: " + user.getEmail()));

        boolean passwordCorrect =
                passwordEncoder.matches(request.getPassword(), credential.getPasswordHash());

        if (!passwordCorrect) {
            throw new RuntimeException("Password incorrecto");
        }

        String token = jwtService.generateToken(
                user.getUserId(),
                user.getEmail(),
                user.getRolId()
        );

        return new LoginResponseDTO(
                token,
                user.getUserId(),
                user.getEmail(),
                user.getRolId()
        );
    }
}