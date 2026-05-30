package com.oraclejavabot.features.users.service;

import com.oraclejavabot.features.auth.model.UserCredentialEntity;
import com.oraclejavabot.features.auth.repository.UserCredentialRepository;
import com.oraclejavabot.features.users.dto.SkillCategory;
import com.oraclejavabot.features.users.dto.SkillCategoryOptionDTO;
import com.oraclejavabot.features.users.dto.UserOperationResponseDTO;
import com.oraclejavabot.features.users.dto.UserRequestDTO;
import com.oraclejavabot.features.users.dto.UserResponseDTO;
import com.oraclejavabot.features.users.dto.UserSkillProfileDTO;
import com.oraclejavabot.features.users.dto.UserUpdateRequestDTO;
import com.oraclejavabot.features.users.model.UserEntity;
import com.oraclejavabot.features.users.repository.UserDependencyRepository;
import com.oraclejavabot.features.users.repository.UserRepository;
import com.oraclejavabot.features.users.repository.UserSkillProfileRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private static final int ESTADO_ACTIVO = 1;
    private static final int ESTADO_INACTIVO = 2;

    private final UserRepository userRepository;
    private final UserSkillProfileRepository userSkillProfileRepository;
    private final UserDependencyRepository userDependencyRepository;
    private final UserCredentialRepository userCredentialRepository;

    public UserService(
            UserRepository userRepository,
            UserSkillProfileRepository userSkillProfileRepository,
            UserDependencyRepository userDependencyRepository,
            UserCredentialRepository userCredentialRepository
    ) {
        this.userRepository = userRepository;
        this.userSkillProfileRepository = userSkillProfileRepository;
        this.userDependencyRepository = userDependencyRepository;
        this.userCredentialRepository = userCredentialRepository;
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
        user.setEstadoId(ESTADO_ACTIVO);

        userRepository.save(user);

        return new UserResponseDTO(
                "Usuario creado correctamente",
                user.getEmail(),
                user.getTelegramId());
    }

    /**
     * Recupera usuarios activos.
     * Para auditoría histórica se podría crear otro endpoint que incluya inactivos.
     */
    public List<UserEntity> getUsers() {
        return userRepository.findByEstadoIdOrderByPrimerNombreAscApellidoAsc(ESTADO_ACTIVO);
    }

    public Optional<UserEntity> findByTelegramId(String telegramId) {
        return userRepository.findByTelegramId(telegramId);
    }

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

    @Transactional
    public UserOperationResponseDTO updateUser(UUID userId, UserUpdateRequestDTO request) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        validateUniqueEmailForUpdate(request.getEmail(), userId);
        validateUniqueTelegramForUpdate(request.getTelegramId(), userId);
        validateManagerRules(userId, request);

        user.setPrimerNombre(request.getPrimerNombre());
        user.setApellido(request.getApellido());
        user.setTelefono(request.getTelefono());
        user.setEmail(request.getEmail());
        user.setTelegramId(request.getTelegramId());
        user.setTelegramChatId(request.getTelegramChatId());
        user.setRolId(request.getRolId());
        user.setEstadoId(request.getEstadoId());
        user.setManagerId(request.getManagerId());

        UserEntity saved = userRepository.save(user);

        syncCredentialStatus(saved);

        return new UserOperationResponseDTO(
                "Usuario actualizado correctamente",
                saved.getUserId().toString(),
                saved.getEmail(),
                saved.getEstadoId()
        );
    }

    @Transactional
    public UserOperationResponseDTO deactivateUser(UUID userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (user.getEstadoId() != null && user.getEstadoId() == ESTADO_INACTIVO) {
            return new UserOperationResponseDTO(
                    "El usuario ya se encontraba inactivo",
                    user.getUserId().toString(),
                    user.getEmail(),
                    user.getEstadoId()
            );
        }

        if (userDependencyRepository.hasAssignedTasks(userId)) {
            throw new IllegalStateException("No se puede desactivar el usuario porque tiene tareas asignadas");
        }

        if (userDependencyRepository.hasActiveDirectReports(userId)) {
            throw new IllegalStateException("No se puede desactivar el usuario porque tiene developers activos asignados como manager");
        }

        user.setEstadoId(ESTADO_INACTIVO);
        UserEntity saved = userRepository.save(user);

        userCredentialRepository.findByUser(saved).ifPresent(credential -> {
            credential.setActivo(0);
            userCredentialRepository.save(credential);
        });

        return new UserOperationResponseDTO(
                "Usuario desactivado correctamente",
                saved.getUserId().toString(),
                saved.getEmail(),
                saved.getEstadoId()
        );
    }

    private void validateUniqueEmailForUpdate(String email, UUID userId) {
        if (userRepository.existsByEmailAndUserIdNot(email, userId)) {
            throw new IllegalArgumentException("Ya existe otro usuario con ese email");
        }
    }

    private void validateUniqueTelegramForUpdate(String telegramId, UUID userId) {
        if (userRepository.existsByTelegramIdAndUserIdNot(telegramId, userId)) {
            throw new IllegalArgumentException("Ya existe otro usuario con ese telegramId");
        }
    }

    private void validateManagerRules(UUID userId, UserUpdateRequestDTO request) {
        if (request.getManagerId() != null && request.getManagerId().equals(userId)) {
            throw new IllegalArgumentException("Un usuario no puede ser su propio manager");
        }

        if (request.getRolId() != null && request.getRolId() == 2 && request.getManagerId() == null) {
            throw new IllegalArgumentException("Un developer debe tener manager");
        }

        if (request.getManagerId() != null) {
            UserEntity manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new IllegalArgumentException("Manager no encontrado"));

            if (manager.getEstadoId() == null || manager.getEstadoId() != ESTADO_ACTIVO) {
                throw new IllegalArgumentException("El manager seleccionado no está activo");
            }

            if (manager.getRolId() == null || manager.getRolId() != 1) {
                throw new IllegalArgumentException("El manager seleccionado debe tener rol de manager");
            }
        }
    }

    private void syncCredentialStatus(UserEntity user) {
        Optional<UserCredentialEntity> credentialOpt = userCredentialRepository.findByUser(user);

        if (credentialOpt.isEmpty()) {
            return;
        }

        UserCredentialEntity credential = credentialOpt.get();
        credential.setActivo(user.getEstadoId() != null && user.getEstadoId() == ESTADO_ACTIVO ? 1 : 0);
        userCredentialRepository.save(credential);
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