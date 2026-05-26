package com.oraclejavabot.features.users.service;

import com.oraclejavabot.features.users.model.UserEntity;
import com.oraclejavabot.features.users.repository.UserRepository;
import com.oraclejavabot.features.users.dto.UserRequestDTO;
import com.oraclejavabot.features.users.dto.UserResponseDTO;

import com.oraclejavabot.features.users.dto.SkillCategory;
import com.oraclejavabot.features.users.dto.UserSkillProfileDTO;
import com.oraclejavabot.features.users.repository.UserSkillProfileRepository;

import com.oraclejavabot.features.users.dto.SkillCategoryOptionDTO;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserSkillProfileRepository userSkillProfileRepository;

    public UserService(
            UserRepository userRepository,
            UserSkillProfileRepository userSkillProfileRepository
    ) {
        this.userRepository = userRepository;
        this.userSkillProfileRepository = userSkillProfileRepository;
    }

    /**
     * Crea un nuevo usuario. Valida que no exista un usuario con el mismo
     * email o telegramId antes de persistir.
     *
     * @param request DTO con los datos del usuario a crear
     * @return DTO con los datos del usuario creado
     * @throws IllegalArgumentException si ya existe un usuario con el mismo email o
     *                                  telegramId
     */
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
                user.getTelegramId());
    }

    /**
     * Recupera todos los usuarios desde la base de datos.
     *
     * @return lista de {@link UserEntity}
     */
    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }

    /**
     * Busca un usuario por su telegramId.
     *
     * @param telegramId identificador de Telegram
     * @return usuario encontrado, si existe
     */
    public Optional<UserEntity> findByTelegramId(String telegramId) {
        return userRepository.findByTelegramId(telegramId);
    }

    /**
     * Busca un usuario por telegramId haciendo trim y fallback case-insensitive.
     *
     * @param telegramId identificador de Telegram
     * @return usuario encontrado, si existe
     */
    public Optional<UserEntity> findByTelegramIdFlexible(String telegramId) {
        if (telegramId == null || telegramId.isBlank()) {
            return Optional.empty();
        }

        String candidate = telegramId.trim();

        Optional<UserEntity> exact = userRepository.findByTelegramId(candidate);
        if (exact.isPresent()) {
            return exact;
        }

        return userRepository.findByTelegramIdIgnoreCase(candidate);
    }

    public List<UserSkillProfileDTO> getUsersSkillProfiles() {
        return userSkillProfileRepository.findAllPrimarySkillProfiles();
    }

    public List<UserSkillProfileDTO> getUsersByPrimarySkillCategory(SkillCategory category) {
        return userSkillProfileRepository.findByPrimarySkillCategory(category);
    }

    public Optional<UserSkillProfileDTO> getUserSkillProfileById(String userIdHex) {
        return userSkillProfileRepository.findByUserId(userIdHex);
    }

    public List<UserSkillProfileDTO> searchUserSkillProfiles(String text) {
        return userSkillProfileRepository.searchSkillProfiles(text);
    }

    public List<SkillCategoryOptionDTO> getSkillCategories() {
        return userSkillProfileRepository.findSkillCategories();
    }

}